package com.ry.yqkj.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : lihy
 * @Description : 订单状态管理
 * @date : 2024/5/27 12:35 上午
 */
public enum OrderStatusEnum {

    ORDER("order", "已下单"),
    PAID("paid", "已支付"),
    SERVICING("servicing", "服务中"),
    DONE("done", "已完成"),
    CANCELED("canceled", "已取消");

    OrderStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;


    public boolean validate(String code) {
        Optional<OrderStatusEnum> opt = Arrays.stream(OrderStatusEnum.values()).filter(a -> code.equals(a.code)).findAny();
        return opt.isPresent();
    }

}
