package com.ry.yqkj.system.component;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.model.req.app.TransferV2Request;
import com.ry.yqkj.model.resp.app.cashwd.TransferBalanceResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferNotifyResp;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.RefundService;
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
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * 微信小程序交易相关组件
 *
 * @author lihy
 */
@Component
@Slf4j
public class WxPayComponent {

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
    private Certificate certificate;

    @Getter
    private CloseableHttpClient closeableHttpClient;

    @Getter
    private NotificationParser notificationParser;

    @PostConstruct
    public void init() throws Exception {
        initConfig();
        initPayConfig();
        initCreateHttpClient();

    }

    private void initPayConfig() {
        // 构建service
        if (service == null) {
            service = new JsapiServiceExtension.Builder().config(config).build();
        }
        if (backService == null) {
            backService = new RefundService.Builder().config(config).build();
        }
    }

    private void initConfig() throws Exception {
        privateKey = this.loadKeyByResource("wechatPay/apiclient_key.pem");
        certificate = loadCertificate(new ClassPathResource("wechatPay/apiclient_cert.pem"));
        if (config == null) {
            config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wxPayConfigProperties.getMchId())
                    .privateKey(privateKey)
                    .merchantSerialNumber(wxPayConfigProperties.getSerialNo())
                    .apiV3Key(wxPayConfigProperties.getApiV3Key())
                    .build();
            notificationParser = new NotificationParser(config);
        }
    }

    /**
     * 企业付款到零钱增加
     */
    private void initCreateHttpClient() throws Exception {
        // 证书路径
        ClassPathResource resource = new ClassPathResource("wechatPay/apiclient_cert.p12");
        // 加载证书
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
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
     * 获取回调通知解析器
     *
     * @param request
     * @return
     * @throws IOException
     */
    public RequestParam buildRequestParam(HttpServletRequest request) throws IOException {
        //获取报文
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        //随机串
        //微信传递过来的签名
        //证书序列号（微信平台）
        //时间戳
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader("Wechatpay-Serial"))
                .nonce(request.getHeader("Wechatpay-Nonce"))
                .signature(request.getHeader("Wechatpay-Signature"))
                .timestamp(request.getHeader("Wechatpay-Timestamp"))
                .body(buffer.toString())
                .build();
        log.info("requestParam={}", requestParam);
        // 如果已经初始化了 RSAAutoCertificateConfig，可以直接使用  config
        // 初始化 NotificationParser
        return requestParam;
    }


    /**
     * 解析微信支付回调参数
     *
     * @param request
     * @throws Exception
     */
    public Transaction notifyPayParser(HttpServletRequest request) throws Exception {
        // 如果已经初始化了 RSAAutoCertificateConfig，可以直接使用  config
        // 初始化 NotificationParser
        // 验签、解密并转换成 Transaction
        return notificationParser.parse(buildRequestParam(request), Transaction.class);
    }

    /**
     * 解析微信商家转账回调参数
     *
     * @param request
     * @throws Exception
     */
//    public TransferNotifyResp notifyTransferParser(HttpServletRequest request) throws Exception {
//        // 验签、解密并转换成 Transaction
//        return notificationParser.parse(buildRequestParam(request), TransferNotifyResp.class);
//    }


    public TransferNotifyResp notifyTransferParser(HttpServletRequest request) throws Exception {
        RequestParam requestParam = buildRequestParam(request);
        Notification notification = JSON.parseObject(requestParam.getBody(),Notification.class);
        // 验签、解密并转换成 Transaction
        byte[] aesKey = wxPayConfigProperties.getApiV3Key().getBytes();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, notification.getResource().getNonce().getBytes());
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        cipher.updateAAD("mch_payment".getBytes());
        String result = new String(cipher.doFinal(Base64.getDecoder().decode(notification.getResource().getCiphertext())), "utf-8");
        return JSON.parseObject(result, TransferNotifyResp.class);
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

    public boolean verifySignature(String data, String signature, String certPath) throws Exception {
        // Load the certificate
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate;
        try (FileInputStream fis = new FileInputStream(certPath)) {
            certificate = factory.generateCertificate(fis);
        }
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(certificate);
        sign.update(data.getBytes());

        // Verify the signature
        byte[] signatureBytes = java.util.Base64.getDecoder().decode(signature);
        return sign.verify(signatureBytes);
    }

    /**
     * 生成微信支付 V3 签名
     *
     * @param signatureData 签名数据
     * @return 签名
     */
    private static String generateSignature(String signatureData, String privateKey) throws Exception {

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


    public TransferBalanceResp transferToBalance(String openid, String withWdNo, BigDecimal amount, String desc) throws Exception {
        // 构建请求体
        String requestBody = buildReqV4(openid, withWdNo, amount, desc);
        log.info("requestBody={}", requestBody);
        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = NonceUtil.createNonce(20);
        String signData = buildSignatureData("POST", "/v3/fund-app/mch-transfer/transfer-bills", timestamp, nonce, requestBody);
        String signature = generateSignature(signData, privateKey);
        // 创建HTTP POST请求
        HttpPost httpPost = new HttpPost(TRANSFER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Wechatpay-Serial", wxPayConfigProperties.getSerialNo());
        String authHeader = buildAuthorizationHeader(timestamp, nonce, signature);
        httpPost.setHeader("Authorization", "WECHATPAY2-SHA256-RSA2048 " + authHeader);
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
        // 发送请求
        CloseableHttpClient httpClient = this.getCloseableHttpClient();
        JSONObject json = new JSONObject(EntityUtils.toString(httpClient.execute(httpPost).getEntity()));

        log.info("json={}", json);

        return TransferBalanceResp.builder()
                .packageInfo(json.getStr("package_info"))
                .transferBillNo(json.getStr("transfer_bill_no"))
                .outBillNo(withWdNo)
                .state(json.getStr("state"))
                .appId(wxPayConfigProperties.getAppId())
                .mchId(wxPayConfigProperties.getMchId())
                .build();
    }

    private String buildReqV4(String openid, String withWdNo, BigDecimal amount, String desc) {
        List<TransferV2Request.transferSceneReportInfo> transferSceneReportInfos = Lists.newArrayList();
        TransferV2Request.transferSceneReportInfo info = new TransferV2Request.transferSceneReportInfo();
        //固定值infoType，不能更改
        info.setInfoType("岗位类型");
        info.setInfoContent("普通员工");
        TransferV2Request.transferSceneReportInfo info2 = new TransferV2Request.transferSceneReportInfo();
        //固定值infoType，不能更改
        info2.setInfoType("报酬说明");
        info2.setInfoContent("收入");
        transferSceneReportInfos.add(info);
        transferSceneReportInfos.add(info2);

        TransferV2Request request = new TransferV2Request();
        request.setAppId(wxPayConfigProperties.getAppId());
        request.setNotifyUrl(wxPayConfigProperties.getConfirmNotifyUrl());
        request.setOpenId(openid);
        request.setTransferAmount(amount.multiply(new BigDecimal("100")).intValue());
        request.setTransferRemark(desc);
        //固定值
        request.setTransferSceneId("1005");
        request.setOutBillNo(withWdNo);
        request.setTransferSceneReportInfos(transferSceneReportInfos);
        return JSON.toJSONString(request);
    }

    /**
     * 构建 Authorization 头
     */
    private String buildAuthorizationHeader(String timestamp, String nonce, String signature) {
        return String.format("mchid=\"%s\",nonce_str=\"%s\",signature=\"%s\",timestamp=\"%s\",serial_no=\"%s\"",
                wxPayConfigProperties.getMchId(), // 商户号
                nonce, // 随机字符串
                signature, // 签名
                timestamp, // 时间戳
                wxPayConfigProperties.getSerialNo()// 证书序列号
        );
    }

    /**
     * 构建验签数据
     */
    public String buildCheckSignatureData(String timestamp, String nonce, String body) {
        return timestamp + "\n" +
                nonce + "\n" +
                body + "\n";
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

    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        String privateKeyPEM = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static PublicKey getPublicKey(String publicKey) throws Exception {
        String publicKeyPEM = publicKey.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private static Certificate loadCertificate(ClassPathResource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return factory.generateCertificate(inputStream);
        }
    }
}
