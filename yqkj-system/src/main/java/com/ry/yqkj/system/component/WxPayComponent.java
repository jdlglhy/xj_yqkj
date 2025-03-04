package com.ry.yqkj.system.component;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.common.utils.uuid.UUID;
import com.ry.yqkj.model.req.app.TransferRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;

/**
 * 微信小程序交易相关组件 copy from https://www.cnblogs.com/yanpeng19940119/p/17693895.html
 *
 * @author lihy
 */
@Component
@Slf4j
public class WxPayComponent {


    private static final String TRANSFER_URL = "https://api.mch.weixin.qq.com/v3/transfer/batches";

    @Resource
    private WxPayConfigProperties wxPayConfigProperties;

    @Getter
    private RSAAutoCertificateConfig config;
    private JsapiServiceExtension service;
    private RefundService backService;
    @Getter
    private CloseableHttpClient closeableHttpClient;

    @PostConstruct
    public void init() throws Exception {
        initConfig();
        initPayConfig();
        initBackConfig();
        initCreateHttpClient();

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
     * 企业付款到零钱增加
     *
     * @return
     * @throws Exception
     */
    private void initCreateHttpClient() throws Exception {
        // 证书路径
        //File certFile = new File("classpath:wechatPay/apiclient_cert.p12");
        ClassPathResource resource = new ClassPathResource("wechatPay/apiclient_cert.p12");
        // 加载证书
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), wxPayConfigProperties.getMchId().toCharArray());
        // 构建SSLContext
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, wxPayConfigProperties.getMchId().toCharArray())
                .build();
        // 创建HttpClient
        closeableHttpClient = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();
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

    /**
     * 向用户转账
     *
     * @throws Exception
     */
    public String transferToBalance(TransferRequest request) throws Exception {
        request.setAppid(wxPayConfigProperties.getAppId());
//        TransferRequest request = new TransferRequest();
//        request.setAppid(wxPayConfigProperties.getAppId());
//        request.setOutBatchNo("");
//        request.setBatchName("");
//        request.setBatchRemark(desc);
//        request.setTotalNum(1);
//        // 单位：分
//        request.setTotalAmount(amount);
//        List<TransferRequest.TransferDetail> detailList = Lists.newArrayList();
//        TransferRequest.TransferDetail detail = new TransferRequest.TransferDetail();
//        detail.setOutDetailNo("");
//        detail.setTransferAmount(amount);
//        detail.setOpenid(openid);
//        detail.setTransferRemark(desc);
//        detailList.add(detail);
//        request.setTransferDetailList(detailList);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        StringBuilder authorization = new StringBuilder();
        authorization.append("WECHATPAY2-SHA256-RSA2048 ");
        authorization.append("mchid=\"").append(wxPayConfigProperties.getMchId()).append("\",");
        authorization.append("timestamp=\"").append(System.currentTimeMillis() / 1000).append("\",");
        authorization.append("serial_no=\"").append(wxPayConfigProperties.getSerialNo()).append("\",");
        authorization.append("signature=\"").append(generateSignature(requestBody,
                wxPayConfigProperties.getApiV3Key())).append("\",");
        authorization.append("nonce_str=\"").append(UUID.randomUUID()).append("\"");
        log.info("authorization={}",authorization.toString());


        // 创建HTTP POST请求
        HttpPost httpPost = new HttpPost(TRANSFER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", authorization.toString());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Wechatpay-Serial",wxPayConfigProperties.getSerialNo());

        // 发送请求
        CloseableHttpClient httpClient = this.getCloseableHttpClient();
        return EntityUtils.toString(httpClient.execute(httpPost).getEntity());
    }

    public void handleCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 读取回调数据
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String callbackData = sb.toString();

        // 验证签名
        if (verifySignature(callbackData, request.getHeader("Wechatpay-Signature"), wxPayConfigProperties.getApiV3Key())) {
            // 处理回调逻辑
            // 解析callbackData，更新订单状态等
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public static String generateSignature(String data, String apiKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifySignature(String data, String signature, String apiKey) throws Exception {
        String expectedSignature = generateSignature(data, apiKey);
        return expectedSignature.equals(signature);
    }


    /**
     * 生成微信支付 V3 签名
     *
     * @param method     请求方法（如 "POST"）
     * @param url        请求 URL（如 "/v3/transfer/batches"）
     * @param timestamp  时间戳（秒级）
     * @param nonce      随机字符串
     * @param body       请求体（JSON 字符串）
     * @param apiKey     商户 API 密钥
     * @return 签名
     */
    private String generateSignature(String method, String url, String timestamp, String nonce, String body,
                                String apiKey) throws Exception {
        // 1. 构建签名数据
        String signatureData = buildSignatureData(method, url, timestamp, nonce, body);

        // 2. 使用 HMAC-SHA256 加密
        byte[] signatureBytes = hmacSha256(signatureData, apiKey);

        // 3. 返回 Base64 编码的签名
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * 构建签名数据
     */
    private static String buildSignatureData(String method, String url, String timestamp, String nonce, String body) {
        return method + "\n" +
                url + "\n" +
                timestamp + "\n" +
                nonce + "\n" +
                body + "\n";
    }

    /**
     * 使用 HMAC-SHA256 加密
     */
    private static byte[] hmacSha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
    public String transferToBalance(String openid, int amount, String desc) throws Exception {
        // 构建请求体
        String requestBody = buildRequestBody(openid, amount, desc);

        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = UUID.randomUUID().toString();
        String signature = this.generateSignature("POST", "/v3/transfer/batches", timestamp, nonce, requestBody,
                wxPayConfigProperties.getApiV3Key());

//        StringBuilder authorization = new StringBuilder();
//        authorization.append("WECHATPAY2-SHA256-RSA2048 ");
//        authorization.append("mchid=\"").append(wxPayConfigProperties.getMchId()).append("\",");
//        authorization.append("timestamp=\"").append(System.currentTimeMillis() / 1000).append("\",");
//        authorization.append("signature=\"").append(generateSignature(requestBody,
//                wxPayConfigProperties.getApiV3Key())).append("\",");
//        authorization.append("nonce_str=\"").append(UUID.randomUUID()).append("\"");
//        log.info("authorization={}",authorization.toString());

        // 创建HTTP POST请求
        HttpPost httpPost = new HttpPost(TRANSFER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
//        httpPost.setHeader("Wechatpay-Serial",wxPayConfigProperties.getSerialNo());
        String authHeader = buildAuthorizationHeader(timestamp,nonce,signature);
        log.info(authHeader);
        httpPost.setHeader("Authorization", "WECHATPAY2-SHA256-RSA2048 " + authHeader);
        log.info(signature);
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        // 发送请求
        CloseableHttpClient httpClient = this.getCloseableHttpClient();
        return EntityUtils.toString(httpClient.execute(httpPost).getEntity());
    }

    private String buildRequestBody(String openid, int amount, String desc) {
        // 构建请求体 JSON
        return String.format("{" +
                "\"appid\": \"%s\"," +
                "\"out_batch_no\": \"%s\"," +
                "\"batch_name\": \"转账\"," +
                "\"batch_remark\": \"%s\"," +
                "\"total_amount\": %d," +
                "\"total_num\": 1," +
                "\"transfer_detail_list\": [{" +
                "\"out_detail_no\": \"%s\"," +
                "\"transfer_amount\": %d," +
                "\"transfer_remark\": \"%s\"," +
                "\"openid\": \"%s\"" +
                "}]" +
                "}", "wxf753d4d1f7dc8bfd", UUID.randomUUID().toString(), desc, amount, UUID.randomUUID().toString(), amount, desc, openid);
    }

    /**
     * 构建 Authorization 头
     */
    private String buildAuthorizationHeader(String timestamp, String nonce, String signature) {
        return String.format("mchid=\"%s\",nonce_str=\"%s\",signature=\"%s\",timestamp=\"%s\",serial_no=\"%s\"",
                "1681292107", // 商户号
                nonce, // 随机字符串
                signature, // 签名
                timestamp, // 时间戳
                "5A31C03313EDBAD2816D48C0F13A9CD7937396FA"// 证书序列号
        );
    }
}
