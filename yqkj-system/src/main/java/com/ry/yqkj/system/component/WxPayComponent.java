package com.ry.yqkj.system.component;

import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.RefundService;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * 微信小程序交易相关组件 copy from https://www.cnblogs.com/yanpeng19940119/p/17693895.html
 */
@Component
public class WxPayComponent {

    @Resource
    private WxPayConfigProperties wxPayConfigProperties;

    @Getter
    private RSAAutoCertificateConfig config;
    private JsapiServiceExtension service;
    private RefundService backService;

    @PostConstruct
    public void init() throws Exception {
        //TODO 支付需要开启
        initConfig();
        initPayConfig();
        initBackConfig();
    }

    private void initPayConfig() {
        // 构建service
        if (service == null) {
            service = new JsapiServiceExtension.Builder().config(config).build();
        }
    }

    private void initBackConfig() {
        // 构建service
        if (backService == null) {
            backService = new RefundService.Builder().config(config).build();
        }
    }

    private void initConfig() throws Exception {
        String privateKey = this.loadKeyByResource("wechatPay/apiclient_key.pem");
        if (config == null) {
            config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wxPayConfigProperties.getMchId())
                    .privateKey(privateKey)
                    .merchantSerialNumber(wxPayConfigProperties.getSerialNo())
                    .apiV3Key(wxPayConfigProperties.getApiV3Key())
                    .build();
        }
    }

    /**
     * 通过文件路径获取文件内容
     * ClassPathResource可以在jar包中运行,但不能使用其中getFile().getPath()
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws Exception 报错信息
     */
    public String loadKeyByResource(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] byteArray = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * 解析微信回调参数
     *
     * @param request
     * @throws Exception
     */
    public Transaction notifyParser(HttpServletRequest request) throws Exception {
        //获取报文
        String body = getRequestBody(request);
        //随机串
        String nonceStr = request.getHeader("Wechatpay-Nonce");
        //微信传递过来的签名
        String signature = request.getHeader("Wechatpay-Signature");
        //证书序列号（微信平台）
        String serialNo = request.getHeader("Wechatpay-Serial");
        //时间戳
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        // 构造 RequestParam
        com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
                .serialNumber(serialNo)
                .nonce(nonceStr)
                .signature(signature)
                .timestamp(timestamp)
                .body(body)
                .build();
        // 如果已经初始化了 RSAAutoCertificateConfig，可以直接使用  config
        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);
        // 验签、解密并转换成 Transaction
        return parser.parse(requestParam, Transaction.class);
    }

    /**
     * 微信小程序预支付接口
     *
     * @param serviceOrder
     * @param openId
     * @return
     */
    public PrepayWithRequestPaymentResponse prepayWithRequestPayment(ServiceOrder serviceOrder, String openId) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        serviceOrder.setTotalAmount(BigDecimal.valueOf(0.01));
        //单位：分
        int totalAmount = (serviceOrder.getTotalAmount().multiply(new BigDecimal(100))).intValue();
        //支付金额
        amount.setTotal(totalAmount);
        amount.setCurrency("CNY");
        // 设置支付成功后的回调
        request.setNotifyUrl(wxPayConfigProperties.getNotifyUrl());
        request.setAmount(amount);
        //支付项目的名称
        request.setAttach(serviceOrder.getOrderNo() + "_订单支付");
        request.setAppid(wxPayConfigProperties.getAppId());
        request.setMchid(wxPayConfigProperties.getMchId());
        //自定义设置支付成功后的商户单号32位字符
        request.setOutTradeNo(serviceOrder.getOrderNo());
        request.setNotifyUrl(wxPayConfigProperties.getNotifyUrl());
        //订单描述
        request.setDescription("购买服务订单");
        Payer payer = new Payer();
        //前端传递的openId
        payer.setOpenid(openId);
        request.setPayer(payer);
        // 调用接口
        return service.prepayWithRequestPayment(request);
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}
