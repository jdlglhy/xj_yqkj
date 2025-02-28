package com.ry.yqkj.system.component;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.common.constant.MsgTemplateConstants;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 微信小程序模版消息发送组件
 */
@Component
@Slf4j
public class WxMsgTemplateComponent {

    @Resource
    private WxPayConfigProperties wxPayConfigProperties;
    @Resource
    private IWxUserService wxService;
    @Resource
    private AssistComponent assistComponent;

    private WxMaService wxMaService;

    @PostConstruct
    public void init() {
        wxMaService = getWxMaService();
    }

    /**
     * 给助教发送新订单消息（提醒助教接单）
     */
    public void sendNewOrderMsg(ServiceOrder serviceOrder) {
        log.debug("sendNewOrderMsg 发送模板订阅消息，提醒用户去支付 orderNo={}", serviceOrder.getOrderNo());
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
            data4.setValue(DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm:ss"));
            dataList.add(data4);
            //等待提供地址进行跳转
            //message.setPage("https://wwww.baidu.com");
            message.setToUser(wxService.getWxUserByCliUserId(assistComponent.getAssistant(serviceOrder.getAssistId()).getCliUserId()).getOpenId());
            log.debug("openId={}",message.getToUser());
            message.setTemplateId(MsgTemplateConstants.NEW_ORDER_NOTIFY_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
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
            data4.setValue(DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm:ss"));
            dataList.add(data4);
            //message.setPage("https://wwww.baidu.com");

            WxMaSubscribeMessage.MsgData data5 = new WxMaSubscribeMessage.MsgData();
            data5.setName("thing5");
            data5.setValue("您的订单已被接单，请尽快完成支付锁定助教！");
            dataList.add(data5);
            message.setToUser(wxService.getWxUserByCliUserId(serviceOrder.getCliUserId()).getOpenId());
            log.debug("openId={}",message.getToUser());
            message.setTemplateId(MsgTemplateConstants.ORDER_PAY_NOTIFY_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
        } catch (Exception e) {
            log.error("sendAcceptOrderMsg error,serviceOrder={}", JSON.toJSON(serviceOrder));
        }
    }


    public void sendPayOrderMsgTest() {
        log.debug("sendPayOrderMsg 发送模板订阅消息，提醒用户去支付 orderNo={}", "dfsdfsdf");
        try {
            WxMaSubscribeMessage message = new WxMaSubscribeMessage();
            List<WxMaSubscribeMessage.MsgData> dataList = Lists.newArrayList();
            WxMaSubscribeMessage.MsgData data1 = new WxMaSubscribeMessage.MsgData();
            data1.setName("character_string6");
            data1.setValue("2025112132");
            dataList.add(data1);
            WxMaSubscribeMessage.MsgData data2 = new WxMaSubscribeMessage.MsgData();
            data2.setName("name3");
            data2.setValue("nichdfddf");
            dataList.add(data2);

            WxMaSubscribeMessage.MsgData data3 = new WxMaSubscribeMessage.MsgData();
            data3.setName("amount7");
            data3.setValue("90");
            dataList.add(data3);

            WxMaSubscribeMessage.MsgData data4 = new WxMaSubscribeMessage.MsgData();
            data4.setName("time4");
            data4.setValue(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
            dataList.add(data4);
            //message.setPage("https://wwww.baidu.com");

            WxMaSubscribeMessage.MsgData data5 = new WxMaSubscribeMessage.MsgData();
            data5.setName("thing5");
            data5.setValue("您的订单已被接单，请尽快完成支付锁定助教！");
            dataList.add(data5);
            message.setToUser("otQEg7eeTizl9qZG3NMgnrEh5XhI");
            log.debug("openId={}",message.getToUser());
            message.setTemplateId(MsgTemplateConstants.ORDER_PAY_NOTIFY_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
        } catch (Exception e) {
            log.error("sendAcceptOrderMsg error", e);
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


    public void sendWxTemplateMessage(ServiceOrder serviceOrder) {
        try {
            WxMaSubscribeMessage message = new WxMaSubscribeMessage();
            List<WxMaSubscribeMessage.MsgData> dataList = Lists.newArrayList();
            WxMaSubscribeMessage.MsgData data1 = new WxMaSubscribeMessage.MsgData();
            data1.setName("character_string2");
            data1.setValue(serviceOrder.getOrderNo());
            dataList.add(data1);
            WxMaSubscribeMessage.MsgData data2 = new WxMaSubscribeMessage.MsgData();
            data2.setName("thing3");
            data2.setValue("已支付");
            dataList.add(data2);

            WxMaSubscribeMessage.MsgData data3 = new WxMaSubscribeMessage.MsgData();
            data3.setName("thing1");
            data3.setValue(serviceOrder.getReserveDur().toString() + "小时助教邀请订单");
            dataList.add(data3);

            WxMaSubscribeMessage.MsgData data4 = new WxMaSubscribeMessage.MsgData();
            data4.setName("time10");
            data4.setValue(DateUtil.format(serviceOrder.getReserveTime(), "yyyy-MM-dd HH:mm:ss"));
            dataList.add(data4);
            //message.setPage("https://wwww.baidu.com");
            message.setToUser(wxService.getWxUserByCliUserId(serviceOrder.getCliUserId()).getOpenId());
            message.setTemplateId(MsgTemplateConstants.ORDER_TM_ID);
            message.setData(dataList);
            wxMaService.getMsgService().sendSubscribeMsg(message);
        } catch (WxErrorException e) {
            log.error("sendWxTemplateMessage error", e);
        }
    }
}
