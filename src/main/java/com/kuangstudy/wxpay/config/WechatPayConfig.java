package com.kuangstudy.wxpay.config;

import com.kuangstudy.wxpay.common.KsdStaticParameter;
import com.kuangstudy.wxpay.utils.WechatPayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatPayConfig implements CommandLineRunner {
    /**
     * 公众号appid
     */
    @Value("${wechat.appId}")
    private String wechatAppId;
    /**
     * 商户号id
     */
    @Value("${wechat.mchId}")
    private String wechatMchId;
    /**
     * 商户序列号
     */
    @Value("${wechat.mchSerialNo}")
    private String mchSerialNo;
    /**
     * 支付key
     */
    @Value("${wechat.v3Key}")
    private String wechatV3Key;
    /**
     * 微信支付回调url
     */
    @Value("${wechat.callback}")
    private String payCallbackUrl;
    /**
     * 统一下单url
     */
    @Value("${wechat.unifiedOrder.url}")
    private String wechatUnifiedOrderUrl;
    /**
     * 平台证书列表地址
     */
    @Value("${wechat.certificates.url}")
    private String wechatCertificatesUrl;
    /**
     * 商户私钥路径
     */
    @Value("${wechat.key.path}")
    private String wechatKeyPath;

    /**
     * logo
     */
    @Value("${wechat.logo}")
    private String logo;

    /**springboot启动的时候会来加载run方法*/
    @Override
    public void run(String... args) throws Exception {
        //微信支付
        KsdStaticParameter.mchId = wechatMchId;
        KsdStaticParameter.appId = wechatAppId;
        KsdStaticParameter.v3Key = wechatV3Key;
        KsdStaticParameter.certificatesUrl = wechatCertificatesUrl;
        KsdStaticParameter.unifiedOrderUrl = wechatUnifiedOrderUrl;
        KsdStaticParameter.notifyUrl = payCallbackUrl;
        KsdStaticParameter.mchSerialNo = mchSerialNo;
        KsdStaticParameter.logo = logo;
        //加载商户私钥
        KsdStaticParameter.privateKey = WechatPayUtils.getPrivateKey(wechatKeyPath);
        //获取平台证书
        // (作为校验，可省略),签名信息已经放在了HttpUtils发起微信支付请求时的请求头中
        //KsdStaticParameter.certificateMap = WechatPayUtils.refreshCertificate();
    }
}