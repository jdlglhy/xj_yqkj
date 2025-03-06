package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 提现管理
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "cash_withdraw")
public class CashWithdraw implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提现单号
     */
    private String withdrawNo;

    /**
     * 外部账单号
     */
    private String transferBillNo;

    /**
     * 用户ID
     */
    private Long accountId;

    /**
     * 提现金额
     */
    private BigDecimal amount;


    /**
     * 备注
     */
    private String remark;

    /**
     * package 信息（商家转账到零钱，拉起用户确认收款的package参数）
     */
    private String packageInfo;


    /**
     * 状态：processing = 处理中、done = 完成、expired = 已失效
     */
    private String status;

    /**
     * 微信转账状态
     */
    private String notifyState;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    /**
     * 修改人
     */
    private String modifyBy;
}
