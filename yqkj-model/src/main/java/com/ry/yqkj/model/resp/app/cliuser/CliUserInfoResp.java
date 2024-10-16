package com.ry.yqkj.model.resp.app.cliuser;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 用户信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CliUserInfoResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 助教ID
     */
    private Long assistId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 0 = 男 1 = 女
     */
    private Integer gender;

    /**
     * 已提现金额 settledAmount
     */
    private BigDecimal totalAmount;
    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;
    /**
     * 可提现金额
     */
    private BigDecimal withdrawAmount;


}
