package com.ry.yqkj.system.component;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.common.constant.MsgConstants;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 微信小程序模版消息发送组件
 *
 * @author lihy
 */
@Component
@Slf4j
public class MsgTemplateComponent {

    @Resource
    private WxPayConfigProperties wxPayConfigProperties;
    @Resource
    private IWxUserService wxService;
    @Resource
    private AssistComponent assistComponent;

    @Value("${sms.accesskeyId}")
    private String accesskeyId;
    @Value("${sms.accesskeySecret}")
    private String accesskeySecret;


    private WxMaService wxMaService;

    private Client smsClient;

    @PostConstruct
    public void init() throws Exception {
        wxMaService = getWxMaService();
        smsClient = createClient();
    }

    /**
     * 给助教发送新订单消息（提醒助教接单）
     */
    public void sendNewOrderMsg(ServiceOrder serviceOrder) {
        log.debug("sendNewOrderMsg 发送模板订阅消息，提醒助教去接单 orderNo={}", serviceOrder.getOrderNo());
        try {
            WxMaSubscribeMessage message = new WxMaSubscribeMessage();
            List<WxMaSubscribeMessage.MsgData> dataList = Lists.newArrayList();
            WxMaSubscribeMessage.MsgData data1 = new WxMaSubscribeMessage.MsgData();
            data1.setName("character_string2");
            data1.setValue(serviceOrder.getOrderNo());
            dataList.add(data1);
            WxMaSubscribeMessage.MsgData data2 = new WxMaSubscribeMessage.MsgData();
            data2.setName("amount4");
            data2.setValue(serviceOrder.getTotalAmount().toString());
            dataList.add(data2);

            WxMaSubscribeMessage.MsgData data3 = new WxMaSubscribeMessage.MsgData();
            data3.setName("thing10");
            data3.setValue(serviceOrder.getServiceAddress());
            dataList.add(data3);

            WxMaSubscribeMessage.MsgData data4 = new WxMaSubscribeMessage.MsgData();
            data4.setName("time31");
            data4.setValue(DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm"));
            dataList.add(data4);
            //等待提供地址进行跳转
            //message.setPage("https://wwww.baidu.com");
            message.setToUser(wxService.getWxUserByCliUserId(assistComponent.getAssistant(serviceOrder.getAssistId()).getCliUserId()).getOpenId());
            log.debug("openId={}", message.getToUser());
            message.setTemplateId(MsgConstants.NEW_ORDER_NOTIFY_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
        } catch (Exception e) {
            log.error("sendNewOrderMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
        //短信
        this.sendNewOrderSmsMsg(serviceOrder);
    }

    /**
     * 短信-给助教发送新订单消息（提醒助教接单）
     */
    public void sendNewOrderSmsMsg(ServiceOrder serviceOrder) {
        log.debug("sendNewOrderSmsMsg 发送短信消息，提醒助教去接单 orderNo={}", serviceOrder.getOrderNo());
        try {
            JSONObject json = new JSONObject();
            json.put("serviceTime", DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm"));
            json.put("hour", serviceOrder.getReserveDur().toString());
            json.put("address",serviceOrder.getBallRoomName());
            String phone = assistComponent.getPhoneByAssistId(serviceOrder.getAssistId());
            sendSmsMsg(MsgConstants.NEW_ORDER_REMIND_SMS_TM_ID, JSON.toJSONString(json), phone);
        } catch (Exception e) {
            log.error("sendNewOrderMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
    }

    /**
     * 给用户发送助教接单的消息（提醒用户支付）
     */
    public void sendPayOrderMsg(ServiceOrder serviceOrder) {
        log.debug("sendPayOrderMsg 发送模板订阅消息，提醒用户去支付 orderNo={}", serviceOrder.getOrderNo());
        try {
            WxMaSubscribeMessage message = new WxMaSubscribeMessage();
            List<WxMaSubscribeMessage.MsgData> dataList = Lists.newArrayList();
            WxMaSubscribeMessage.MsgData data1 = new WxMaSubscribeMessage.MsgData();
            data1.setName("character_string6");
            data1.setValue(serviceOrder.getOrderNo());
            dataList.add(data1);
            WxMaSubscribeMessage.MsgData data2 = new WxMaSubscribeMessage.MsgData();
            data2.setName("name3");
            data2.setValue(assistComponent.getAssistant(serviceOrder.getAssistId()).getNickName());
            dataList.add(data2);

            WxMaSubscribeMessage.MsgData data3 = new WxMaSubscribeMessage.MsgData();
            data3.setName("amount7");
            data3.setValue(serviceOrder.getTotalAmount().toString());
            dataList.add(data3);

            WxMaSubscribeMessage.MsgData data4 = new WxMaSubscribeMessage.MsgData();
            data4.setName("time4");
            data4.setValue(DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm"));
            dataList.add(data4);
            //message.setPage("https://wwww.baidu.com");

            WxMaSubscribeMessage.MsgData data5 = new WxMaSubscribeMessage.MsgData();
            data5.setName("thing5");
            data5.setValue("您的订单已被接单，请尽快完成支付锁定助教！");
            dataList.add(data5);
            message.setToUser(wxService.getWxUserByCliUserId(serviceOrder.getCliUserId()).getOpenId());
            log.debug("openId={}", message.getToUser());
            message.setTemplateId(MsgConstants.ORDER_PAY_NOTIFY_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
        } catch (Exception e) {
            log.error("sendAcceptOrderMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
        //短信
        sendPayOrderSmsMsg(serviceOrder);
    }

    /**
     * 短信-给用户发送助教接单的消息（提醒用户支付）
     */
    public void sendPayOrderSmsMsg(ServiceOrder serviceOrder) {
        log.debug("sendPayOrderSmsMsg 发送短信消息，提醒用户支付 orderNo={}", serviceOrder.getOrderNo());
        try {
            JSONObject json = new JSONObject();
            json.put("orderNo", serviceOrder.getOrderNo());
            String phone = assistComponent.getPhoneByUserId(serviceOrder.getCliUserId());
            sendSmsMsg(MsgConstants.ACCEPT_ORDER_REMIND_SMS_TM_ID, JSON.toJSONString(json), phone);
        } catch (Exception e) {
            log.error("sendPayOrderSmsMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
    }

    /**
     * 短信-给助教发送短信，提醒用户已经支付成功
     */
    public void sendPayDoneOrderSmsMsg(ServiceOrder serviceOrder) {
        log.debug("sendPayDoneOrderSmsMsg 发送短信消息，提醒用户支付 orderNo={}", serviceOrder.getOrderNo());
        try {
            JSONObject json = new JSONObject();
            json.put("orderNo", serviceOrder.getOrderNo());
            json.put("serviceTime", DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm"));
            json.put("hour", serviceOrder.getReserveDur().toString());
            json.put("address", serviceOrder.getBallRoomName());
            String phone = assistComponent.getPhoneByAssistId(serviceOrder.getAssistId());
            sendSmsMsg(MsgConstants.ORDER_PAY_DONE_SMS_TM_ID, JSON.toJSONString(json), phone);
        } catch (Exception e) {
            log.error("sendPayOrderSmsMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
    }

    private WxMaService getWxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(wxPayConfigProperties.getAppId());
        wxMaConfig.setSecret(wxPayConfigProperties.getAppSecret());
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }

    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     *
     * @return Client
     * @throws Exception
     */
    private Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(accesskeyId)
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(accesskeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    private void sendSmsMsg(String templateCode, String param, String phone) {
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setTemplateParam(param);
        smsRequest.setTemplateCode(templateCode);
        smsRequest.setPhoneNumbers(phone);
        smsRequest.setSignName(MsgConstants.sign);
        try {
            SendSmsResponse sendSmsResponse = smsClient.sendSms(smsRequest);
            log.info("sendSmsMsg success!phone={}，resp={}", phone,JSON.toJSON(sendSmsResponse));
        } catch (Exception e) {
            log.error("sendSmsMsg error,phone = {},smsRequest={}", phone, smsRequest, e);
        }
    }
}
