package com.ry.yqkj.model.req.app.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 订单预支付
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderPrepareReq implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

}
