package com.ry.yqkj.model.enums;

import lombok.Getter;

/**
 * 用户类型
 */
@Getter
public enum UserTypeEnum {
    SYSTEM("00", "系统用户"),
    CLI_USER("01", "小程序"),
    ASSISTANT("02", "助教"),
    AREA_AGENT("03", "区域代理"),
    BILLIARD_HALL("05", "球馆");

    UserTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public final String code;
    public final String message;
}
