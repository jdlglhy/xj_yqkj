package com.ry.yqkj.model.resp.app.fund;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 资金情况
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class FundInfoResp implements Serializable {

    /**
     * 总金额 settledAmount
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
