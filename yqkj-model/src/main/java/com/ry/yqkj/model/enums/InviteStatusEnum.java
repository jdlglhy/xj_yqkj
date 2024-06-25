package com.ry.yqkj.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : lihy
 * @Description : 邀约状态管理
 * @date : 2024/5/27 12:35 上午
 */
public enum InviteStatusEnum {

    INVITED("invited", "已邀约"),
    RECEIVED("received", "已接单"),
    REFUSED("refused", "已拒绝");

    InviteStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;


    public boolean validate(String code) {
        Optional<InviteStatusEnum> opt = Arrays.stream(InviteStatusEnum.values()).filter(a -> code.equals(a.code)).findAny();
        return opt.isPresent();
    }

}
