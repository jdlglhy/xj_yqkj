package com.ry.yqkj.model.enums;

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


}
