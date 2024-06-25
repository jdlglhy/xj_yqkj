package com.ry.yqkj.common.config.wxpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : lihy
 * @Description : 微信支付信息配置
 * @date : 2024/5/19 11:14 下午
 */
@Component
@Data
@ConfigurationProperties(prefix = "wechatpay")
public class WxPayConfigProperties {
    /**
     * 小程序appId
     */
    private String appId;
    /**
     * 小程序密钥
     */
    private String appSecret;
    /**
     * 商户号（微信支付平台设置）
     */
    private String mchId;
    /**
     * APIv3密钥（微信支付平台设置）
     */
    private String apiV3Key;
    /**
     * 支付成功回调地址
     */
    private String notifyUrl;
    /**
     * 商户证书序列号
     */
    private String serialNo;
}
