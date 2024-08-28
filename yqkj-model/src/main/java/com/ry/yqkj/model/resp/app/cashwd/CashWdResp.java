package com.ry.yqkj.model.resp.app.cashwd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 提现返回信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CashWdResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：processing = 处理中、done = 完成、expired = 已失效
     */
    private String status;
    /**
     * 提现时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
