package com.ry.yqkj.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 微信授权请求
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WxAuthorizeReq implements Serializable {

    /**
     * openId 微信ID
     */
    @ApiModelProperty(value = "code",required = true)
    @NotBlank(message = "code不能为空")
    private String code;
}
