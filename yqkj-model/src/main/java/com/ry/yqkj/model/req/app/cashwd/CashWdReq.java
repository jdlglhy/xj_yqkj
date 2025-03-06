package com.ry.yqkj.model.req.app.cashwd;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 提现申请
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CashWdReq implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "提现金额必须大于0")
    private BigDecimal amount;
}
