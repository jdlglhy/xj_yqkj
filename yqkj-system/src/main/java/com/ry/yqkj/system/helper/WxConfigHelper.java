package com.ry.yqkj.system.helper;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.ry.yqkj.common.config.wxpay.WxPayConfigProperties;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/*
 * since: 2025/3/6 10:50
 * author: lihy
 * description: wx初始化
 */
@Component
public class WxConfigHelper {

    @Resource
    private WxPayConfigProperties wxPayConfigProperties;
    @Getter
    private static WxMaService wxMaService;

    @PostConstruct
    public void init() {
        wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(wxPayConfigProperties.getAppId());
        wxMaConfig.setSecret(wxPayConfigProperties.getAppSecret());
        wxMaService.setWxMaConfig(wxMaConfig);
    }
}
