package com.ry.yqkj.system.component;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.common.utils.uuid.UUID;
import com.ry.yqkj.model.req.app.TransferRequest;
import com.ry.yqkj.model.req.app.TransferV2Request;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.transferbatch.TransferBatchService;
import com.wechat.pay.java.service.transferbatch.model.InitiateBatchTransferRequest;
import com.wechat.pay.java.service.transferbatch.model.InitiateBatchTransferResponse;
import com.wechat.pay.java.service.transferbatch.model.TransferDetailInput;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
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
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 微信小程序交易相关组件 copy from https://www.cnblogs.com/yanpeng19940119/p/17693895.html
 *
 * @author lihy
 */
@Component
@Slf4j
public class WxPayComponent {


    //private static final String TRANSFER_URL = "https://api.mch.weixin.qq.com/v3/transfer/batches";

    private static final String TRANSFER_URL = "https://api.mch.weixin.qq.com/v3/fund-app/mch-transfer/transfer-bills";
    @Resource
    private WxPayConfigProperties wxPayConfigProperties;

    @Getter
    private RSAAutoCertificateConfig config;
    private JsapiServiceExtension service;
    private RefundService backService;

    @Getter
    private String privateKey;

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
        privateKey = this.loadKeyByResource("wechatPay/apiclient_key.pem");
        if (config == null) {
            config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wxPayConfigProperties.getMchId())
                    .privateKey(privateKey)
                    .merchantSerialNumber(wxPayConfigProperties.getSerialNo())
                    .apiV3Key(wxPayConfigProperties.getApiV3Key())
                    .build();
        }
    }

    public void transfer(String openId, Long amount, String remark) {
        TransferBatchService transferBatchService = new TransferBatchService.Builder().config(config).build();
        InitiateBatchTransferRequest request = new InitiateBatchTransferRequest();
        request.setAppid(wxPayConfigProperties.getAppId());
        request.setOutBatchNo(NonceUtil.createNonce(18));
        request.setTotalAmount(amount);
        request.setBatchName(remark);
        request.setBatchRemark(remark);
        request.setTotalNum(1);
        List<TransferDetailInput> transferDetailList = new ArrayList();
        TransferDetailInput transferDetailInput = new TransferDetailInput();
        transferDetailInput.setOpenid(openId);
        transferDetailInput.setOutDetailNo(NonceUtil.createNonce(18));
        transferDetailInput.setTransferAmount(amount);
        transferDetailInput.setTransferRemark(remark);
        transferDetailList.add(transferDetailInput);
        request.setTransferDetailList(transferDetailList);
        InitiateBatchTransferResponse response = transferBatchService.initiateBatchTransfer(request);
        log.info("response={}", response);
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
        //File certFile = new File(WxPayComponent.class.getClassLoader().getResource("wechatPay/apiclient_cert.p12").getFile());
        ClassPathResource resource = new ClassPathResource("wechatPay/apiclient_cert.p12");
        // 加载证书
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //try (FileInputStream instream = new FileInputStream(certFile)) {
        keyStore.load(resource.getInputStream(), wxPayConfigProperties.getMchId().toCharArray()); // 使用商户号作为证书密码
        //}
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
        log.info("authorization={}", authorization.toString());


        // 创建HTTP POST请求
        HttpPost httpPost = new HttpPost(TRANSFER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", authorization.toString());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Wechatpay-Serial", wxPayConfigProperties.getSerialNo());

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
     * @param method    请求方法（如 "POST"）
     * @param url       请求 URL（如 "/v3/transfer/batches"）
     * @param timestamp 时间戳（秒级）
     * @param nonce     随机字符串
     * @param body      请求体（JSON 字符串）
     * @param apiKey    商户 API 密钥
     * @return 签名
     */
    private String generateSignature(String method, String url, String timestamp, String nonce, String body,
                                     String apiKey) throws Exception {
        // 1. 构建签名数据
        String signatureData = buildSignatureData(method, url, timestamp, nonce, body);
        log.info("签名参数：{}", signatureData);

//        String priCont = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
//                .replace("-----END PRIVATE KEY-----", "")
//                .replaceAll("\\s+", "");
//
//        // 2. 使用 HMAC-SHA256 加密
//        byte[] signatureBytes = hmacSha256(signatureData, priCont);
//
//        log.info("使用 HMAC-SHA256 加密后 = {}",new String(signatureBytes));
//
//        // 3. 返回 Base64 编码的签名
//        String sign = Base64.getEncoder().encodeToString(signatureBytes);
//        log.info("base64 = {}",sign);
//        return sign;

//        Signature signature = Signature.getInstance("SHA256withRSA");
//        signature.initSign(apiKey);
//        signature.update(signatureData.getBytes(StandardCharsets.UTF_8));
//        byte[] signatureBytes = signature.sign();
//
//        // 3. 返回 Base64 编码的签名
//        String sign = Base64.getEncoder().encodeToString(signatureBytes);
//        log.info("base64 = {}", sign);


//        Signature signature = Signature.getInstance("SHA256withRSA");
//        PrivateKey privateKey = loadPrivateKey(wxPayConfigProperties.getMchId());
//        log.info("privateKey={}",privateKey);
//        signature.initSign(loadPrivateKey(wxPayConfigProperties.getMchId()));
//        signature.update(signatureData.getBytes(StandardCharsets.UTF_8));
//        byte[] signBytes = signature.sign();
//        return Base64.getEncoder().encodeToString(signBytes);


   // 2. 使用 SHA-256 with RSA 加密
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getPrivateKey(privateKey));
        sign.update(signatureData.getBytes("utf-8"));

        byte[] signatureBytes = sign.sign();

        log.info("使用 SHA-256 with RSA 加密后 = {}", new String(signatureBytes));

        // 3. 返回 Base64 编码的签名
        String signBase64 = Base64.getEncoder().encodeToString(signatureBytes);
        log.info("base64 = {}", signBase64);
        return signBase64;
    }

    public static PrivateKey loadPrivateKey(String mchId) throws Exception {
        // 1. 加载 .p12 文件
        ClassPathResource resource = new ClassPathResource("wechatPay/apiclient_cert.p12");
        // 2. 初始化 KeyStore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), mchId.toCharArray());

        // 3. 获取私钥
        String alias = keyStore.aliases().nextElement(); // 获取第一个别名
        return (PrivateKey) keyStore.getKey(alias, mchId.toCharArray());
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

    private PrivateKey getPrivateKey(String privateKey) throws Exception {
        String privateKeyPEM = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 使用 HMAC-SHA256 加密123123
     */
    private static byte[] hmacSha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public String transferToBalance(String openid, int amount, String desc) throws Exception {
        // 构建请求体
        String requestBody = buildReqV4(openid, amount, desc);
        log.info("requestBody={}",requestBody);

        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = NonceUtil.createNonce(20);
        String signature = this.generateSignature("POST", "/v3/fund-app/mch-transfer/transfer-bills", timestamp, nonce, requestBody,
                wxPayConfigProperties.getApiV3Key());
        log.info("signature={}", signature);

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
        httpPost.setHeader("Wechatpay-Serial", wxPayConfigProperties.getSerialNo());
        String authHeader = buildAuthorizationHeader(timestamp, nonce, signature);
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
                "}", "wxf753d4d1f7dc8bfd", NonceUtil.createNonce(16), desc, amount, NonceUtil.createNonce(16), amount, desc, openid);
    }
    private String buildRequestBodyV3(String openid, int amount, String desc) {



        // 构建请求体 JSON
        return String.format("{" +
                "\"appid\": \"%s\"," +
                "\"out_bill_no\": \"%s\"," +
                "\"transfer_scene_id\": \"1005\"," +
                "\"transfer_amount\": %d," +
                "\"transfer_remark\": \"%s\"," +
                "\"openid\": \"%s\"," +
                "\"transfer_scene_report_infos\": [{" +
                "\"info_type\": \"奖励说明\"," +
                "\"info_content\": \"佣金收入\"" +
                "}]" +

                "}", "wxf753d4d1f7dc8bfd", NonceUtil.createNonce(16), amount,desc, openid);
    }

    private String buildReqV4(String openid, int amount, String desc){
        List<TransferV2Request.transferSceneReportInfo> transferSceneReportInfos = Lists.newArrayList();
        TransferV2Request.transferSceneReportInfo info = new TransferV2Request.transferSceneReportInfo();
        info.setInfoType("岗位类型");
        info.setInfoContent("助教");

        TransferV2Request.transferSceneReportInfo info2 = new TransferV2Request.transferSceneReportInfo();
        info2.setInfoType("报酬说明");
        info2.setInfoContent("助教订单收入");

        TransferV2Request.transferSceneReportInfo info3 = new TransferV2Request.transferSceneReportInfo();
        info3.setInfoType("企业补贴");
        info3.setInfoContent("企业补贴2");

        TransferV2Request.transferSceneReportInfo info4 = new TransferV2Request.transferSceneReportInfo();
        info4.setInfoType("开工利是");
        info4.setInfoContent("开工利是3");

        transferSceneReportInfos.add(info);
        transferSceneReportInfos.add(info2);
//        transferSceneReportInfos.add(info3);
//        transferSceneReportInfos.add(info4);


        TransferV2Request request = new TransferV2Request();
        request.setAppId("wxf753d4d1f7dc8bfd");
        request.setOpenId(openid);
        request.setTransferAmount(amount);
        request.setTransferRemark(desc);
        request.setTransferSceneId("1005");
        request.setOutBillNo(NonceUtil.createNonce(16));
        //request.setUserRecvPerception("劳务报酬");
        request.setTransferSceneReportInfos(transferSceneReportInfos);
        return JSON.toJSONString(request);
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
