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
 * @Description : 资金管理
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "fund")
public class Fund implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 已结算金额 settledAmount
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
