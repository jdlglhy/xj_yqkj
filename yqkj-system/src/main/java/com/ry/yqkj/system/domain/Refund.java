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
 * @Description : 退款实体
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "refund")
public class Refund implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 退款状态 status   待审批 （approving）、通过（approved）、拒绝（refused）
     */
    private String status;

    /**
     * 申请退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 实际退款金额
     */
    private BigDecimal actualRefundAmount;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 用户备注
     */
    private String userRemark;

    /**
     * 审批备注
     */
    private String approveRemark;
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
