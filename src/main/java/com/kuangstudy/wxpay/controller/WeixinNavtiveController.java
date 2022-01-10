package com.kuangstudy.wxpay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kuangstudy.wxpay.common.KsdStaticParameter;
import com.kuangstudy.wxpay.entity.Course;
import com.kuangstudy.wxpay.entity.UserPay;
import com.kuangstudy.wxpay.service.CourseService;
import com.kuangstudy.wxpay.service.UserPayService;
import com.kuangstudy.wxpay.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
public class WeixinNavtiveController {

    @Autowired
    CourseService courseService;
    @Autowired
    UserPayService userPayService;

    /**
     * 付款订单Api,根据传入的订单号 生成付款二维码
     *
     * @param response
     */
    @RequestMapping("/weixinpay")
    @ResponseBody
    public byte[] weixinpay(String courseid, HttpServletResponse response) throws JsonProcessingException {
        if (StringUtils.isEmpty(courseid)) return null;
        Course course = courseService.getById(courseid);
        if (course == null) return null;
        //1:封装请求参数
        Map<String, Object> map = new HashMap();
        map.put("mchid", KsdStaticParameter.mchId);
        map.put("appid", KsdStaticParameter.appId);
        map.put("out_trade_no", new SnowflakeIdWorker(1, 1).nextId() + "");
        //临时写死配置
        map.put("description", course.getTitle());
        map.put("notify_url", KsdStaticParameter.notifyUrl);
        Map<String, Object> amount = new HashMap();
        //订单金额 单位分
        amount.put("total", Integer.parseInt(getMoney(course.getPrice())));
        amount.put("currency", "CNY");
        map.put("amount", amount);

        // 附属参数
        Map<String, Object> attachMap = new HashMap();
        attachMap.put("courseid", courseid);
        attachMap.put("userid", 1);
        attachMap.put("price", course.getPrice());
        attachMap.put("nickname", "飞哥");
        map.put("attach", JsonUtil.obj2String(attachMap));

        // 2:转换成json字符串，开始微信支付请求
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(map);

        // 3:请求统一微信native下单接口
        Map<String, Object> stringObjectMap = HttpUtils.doPost(KsdStaticParameter.unifiedOrderUrl, body);

        String codeUrl = stringObjectMap.get("code_url").toString();
        //生成二维码配置
        try {
            // 7: 生成微信支付二维码
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            String logopath = ResourceUtils.getFile(KsdStaticParameter.logo).getAbsolutePath();
            BufferedImage buff = QRCodeUtil.encode(codeUrl, logopath, false);
            ImageOutputStream imageOut = ImageIO.createImageOutputStream(output);
            ImageIO.write(buff, "JPEG", imageOut);
            imageOut.close();
            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            return FileCopyUtils.copyToByteArray(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 支付回调通知
     *
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/api/pay/callback")
    public Map orderPayCallback(@RequestBody Map body, HttpServletRequest request) {
        log.info("1----------->微信支付回调开始");
        Map<String, Object> result = new HashMap();
        //1：获取微信支付回调的获取签名信息
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String nonce = request.getHeader("Wechatpay-Nonce");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 2: 开始解析报文体
            String data = objectMapper.writeValueAsString(body);
            String message = timestamp + "\n" + nonce + "\n" + data + "\n";
            //3：获取应答签名
            String sign = request.getHeader("Wechatpay-Signature");
            //4：获取平台对应的证书
            String serialNo = request.getHeader("Wechatpay-Serial");
            if (!KsdStaticParameter.certificateMap.containsKey(serialNo)) {
                KsdStaticParameter.certificateMap = WechatPayUtils.refreshCertificate();
            }
            X509Certificate x509Certificate = KsdStaticParameter.certificateMap.get(serialNo);
            if (!WechatPayUtils.verify(x509Certificate, message.getBytes(), sign)) {
                throw new IllegalArgumentException("微信支付签名验证失败:" + message);
            }
            log.info("签名验证成功");
            Map<String, String> resource = (Map) body.get("resource");
            // 5：回调报文解密
            AesUtil aesUtil = new AesUtil(KsdStaticParameter.v3Key.getBytes());
            //解密后json字符串
            String decryptToString = aesUtil.decryptToString(
                    resource.get("associated_data").getBytes(),
                    resource.get("nonce").getBytes(),
                    resource.get("ciphertext"));
            log.info("2------------->decryptToString====>{}", decryptToString);
            //6：获取微信支付返回的信息
            Map<String, Object> jsonData = objectMapper.readValue(decryptToString, Map.class);
            //7: 支付状态的判断 如果是success就代表支付成功
            if ("SUCCESS".equals(jsonData.get("trade_state"))) {
                // 8：获取支付的交易单号，流水号，和附属参数
                String out_trade_no = jsonData.get("out_trade_no").toString();
                String transaction_id = jsonData.get("transaction_id").toString();
                // 附属参数
                String attach = jsonData.get("attach").toString();
                //TODO 根据订单号查询支付状态，如果未支付，更新支付状态 为已支付
                log.info("3----------->微信支付成功,支付流水号是：{},附属参数是：{}", out_trade_no, attach);
                log.info("4----------->微信支付成功,支付流水号是：{},{}", transaction_id);
                // 把附属参数转换成map
                HashMap<String,Object> hashMap = JsonUtil.string2Obj(attach, HashMap.class);
                // 保存用户订单明细
                UserPay userPay = new UserPay();
                userPay.setUserid(String.valueOf(hashMap.get("userid")));
                userPay.setNickname(String.valueOf(hashMap.get("nickname")));
                userPay.setCourseid(String.valueOf(hashMap.get("courseid")));
                userPay.setPrice(String.valueOf(hashMap.get("price")));
                userPay.setTradeno(transaction_id);
                userPay.setCreateTime(new Date());
                userPayService.saveOrUpdate(userPay);
            }
            result.put("code", "SUCCESS");
            result.put("message", "成功");
        } catch (Exception e) {
            result.put("code", "fail");
            result.put("message", "系统错误");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 元转换成分
     *
     * @param money
     * @return
     */
    private static String getMoney(String money) {
        if (money == null || money.equalsIgnoreCase("0")) {
            return "";
        }
        // 金额转化为分为单位
        // 处理包含, ￥ 或者$的金额
        String currency = money.replaceAll("\\$|\\￥|\\,", "");
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }
}