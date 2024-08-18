package com.ry.yqkj.system.component;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.date.DateUtil;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import com.ry.yqkj.common.constant.MsgTemplateConstants;
import com.ry.yqkj.common.utils.DateUtils;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

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

    public void sendWxTemplateMessage(Long cliUserId) {
        try {
//            WxUser wxUser = wxService.getWxUserByCliUserId(cliUserId);
//            if (wxUser == null) {
//                log.error("sendWxTemplateMessage error. cliUserId:{}", cliUserId);
//                return;
//            }
            WxMaService wxMaService = new WxMaServiceImpl();
            WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
            wxMaConfig.setAppid(wxPayConfigProperties.getAppId());
            wxMaConfig.setSecret(wxPayConfigProperties.getAppSecret());
            wxMaService.setWxMaConfig(wxMaConfig);
            for(int i = 0;i< 20;i ++){


                WxMaSubscribeMessage message = new WxMaSubscribeMessage();
                List<WxMaSubscribeMessage.MsgData> dataList = Lists.newArrayList();
                WxMaSubscribeMessage.MsgData data1 = new WxMaSubscribeMessage.MsgData();
                data1.setName("character_string2");
                data1.setValue("S2024080623232232323");
                dataList.add(data1);
                WxMaSubscribeMessage.MsgData data2 = new WxMaSubscribeMessage.MsgData();
                data2.setName("thing3");
                data2.setValue("待接单");
                dataList.add(data2);

                WxMaSubscribeMessage.MsgData data3 = new WxMaSubscribeMessage.MsgData();
                data3.setName("thing1");
                data3.setValue("助教邀约订单");
                dataList.add(data3);

                WxMaSubscribeMessage.MsgData data4 = new WxMaSubscribeMessage.MsgData();
                data4.setName("time10");
                data4.setValue(DateUtil.format(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
                dataList.add(data4);
                message.setPage("https://wwww.baidu.com");
                message.setToUser("otQEg7az63HDSoPSrGWoIWm1-JhU");
                message.setTemplateId(MsgTemplateConstants.ORDER_TM_ID);
                message.setData(dataList);
                wxMaService.getMsgService().sendSubscribeMsg(message);
            }

        } catch (WxErrorException e) {
            log.error("sendWxTemplateMessage error", e);
        }
    }
}
