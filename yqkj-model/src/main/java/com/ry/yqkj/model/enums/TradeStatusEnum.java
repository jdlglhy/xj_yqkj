package com.ry.yqkj.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : lihy
 * @Description : 交易状态管理
 * @date : 2024/5/27 12:35 上午
 */
public enum TradeStatusEnum {

    PROCESSING("processing", "处理中"),
    DONE("done", "完成"),
    EXPIRED("expired", "已失效");

    TradeStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;


    public boolean validate(String code) {
        Optional<TradeStatusEnum> opt = Arrays.stream(TradeStatusEnum.values()).filter(a -> code.equals(a.code)).findAny();
        return opt.isPresent();
    }

}
