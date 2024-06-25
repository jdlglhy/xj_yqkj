package com.ry.yqkj.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : lihy
 * @Description : 模块前缀管理
 * @date : 2024/5/27 12:35 上午
 */
public enum ModulePreFixEnum {

    SERVICE_ORDER("SN", "服务订单号前缀"),
    REFUND("RN", "退款单"),
    TRADE("TN", "交易流水");

    ModulePreFixEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;


    public boolean validate(String code) {
        Optional<ModulePreFixEnum> opt = Arrays.stream(ModulePreFixEnum.values()).filter(a -> code.equals(a.code)).findAny();
        return opt.isPresent();
    }

}
