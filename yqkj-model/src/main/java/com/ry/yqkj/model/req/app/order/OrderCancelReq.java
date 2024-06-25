package com.ry.yqkj.model.req.app.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 订单取消请求参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderCancelReq implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", required = false)
    @Length(max = 64, message = "备注不能超过64字符")
    private String remark;


}
