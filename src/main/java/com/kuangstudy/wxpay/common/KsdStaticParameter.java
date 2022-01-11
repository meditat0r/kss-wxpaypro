package com.kuangstudy.wxpay.common;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 徐柯
 * @Description 定义微信支付参数配置类
 * @Date 21:06 2021/5/10
 * @Param
 * @return
 **/
public class KsdStaticParameter {
    /**
     * 微信商户号
     */
    public static String mchId;
    /**
     * 商户在微信公众平台申请服务号对应的APPID
     */
    public static String appId;
    /**
     * 回调报文解密V3密钥key
     */
    public static String v3Key;
    /**
     * 微信获取平台证书列表地址
     */
    public static String certificatesUrl;
    /**
     * 微信APP下单URL
     */
    public static String unifiedOrderUrl;
    /**
     * 微信小程序的单URL
     */
    public static String unifiedOrderUrlJS;
    /**
     * 异步接收微信支付结果通知的回调地址
     */
    public static String notifyUrl;
    /**
     * 微信证书私钥
     */
    public static PrivateKey privateKey;

    /**
     * logo
     */
    public static String logo;
    /**
     * 微信商家api序列号
     */
    public static String mchSerialNo;
    /**
     * 定义全局容器 保存微信平台证书公钥
     */
    public static Map<String, X509Certificate> certificateMap = new ConcurrentHashMap<>();

    public static void certificateMap(String serialNo) {
    }
}