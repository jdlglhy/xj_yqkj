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
 * @Description : 交易流水
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "trade")
public class Trade implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 订单编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 交易流水号
     */
    private String tradeNo;
    /**
     * 业务标识
     */
    private String bizNo;
    /**
     * 交易类型： 订单支付、退款、提现..
     */
    private String type;
    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 状态 processing = 处理中、done = 交易完成
     */
    private String status;


    /**
     * 备注
     */
    private String remark;

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
