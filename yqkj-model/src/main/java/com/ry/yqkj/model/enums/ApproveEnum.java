package com.ry.yqkj.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : lihy
 * @Description : 审批状态枚举
 * @date : 2024/5/27 12:35 上午
 */
public enum ApproveEnum {

    APPROVING("approving","审批中"),
    APPROVED("approved","通过"),
    REFUSED("refused","不通过");

    ApproveEnum(String code,String message){
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;


    public boolean validate(String code){
        Optional<ApproveEnum> opt = Arrays.stream(ApproveEnum.values()).filter(a->code.equals(a.code)).findAny();
        return opt.isPresent();
    }

}
