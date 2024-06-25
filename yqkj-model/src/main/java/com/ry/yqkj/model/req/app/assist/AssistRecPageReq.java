package com.ry.yqkj.model.req.app.assist;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 推荐助教分页列表
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistRecPageReq implements Serializable {
    /**
     * 省
     */
    @ApiModelProperty(value = "省", required = true)
    @NotBlank(message = "省份")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty(value = "市", required = true)
    @NotBlank(message = "城市")
    private String city;
    /**
     * 所在 区、县
     */
    @ApiModelProperty(value = "区、县", required = true)
    @NotBlank(message = "区、县")
    private String county;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", required = true, notes = "默认 = 1")
    private Integer gender = 1;
}
