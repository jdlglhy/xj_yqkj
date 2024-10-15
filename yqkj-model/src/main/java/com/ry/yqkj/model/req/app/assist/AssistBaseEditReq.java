package com.ry.yqkj.model.req.app.assist;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : lihy
 * @Description : 助教基本信息修改
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistBaseEditReq implements Serializable {

    @ApiModelProperty(value = "助教ID", required = false)
    @NotNull(message = "助教ID必填")
    private Long assistId;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "宝贝昵称", required = false, notes = "不填写设置为微信昵称")
    @Length(max = 64)
    private String nickName;
    /**
     * 费用
     */
    @ApiModelProperty(value = "费用", required = false, example = "58、68、78、88、98", notes = "单位：元/小时")
    @NotNull(message = "请设置费用")
    private BigDecimal price = BigDecimal.valueOf(0.01);
    /**
     * 生活照
     */
    @ApiModelProperty(value = "生活照（最多6张）", required = true)
    @NotEmpty(message = "请上传生活照")
    private List<String> lifePhotos;
    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", required = true)
    @NotBlank(message = "请上传高清头像")
    private String avatar;
    /**
     * 省
     */
    @ApiModelProperty(value = "省", required = true)
    @NotBlank(message = "请选择所在省份")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty(value = "市", required = true)
    @NotBlank(message = "请选择所在市")
    private String city;
    /**
     * 所在 区、县
     */
    @ApiModelProperty(value = "所在区、县", required = true)
    @NotBlank(message = "请选择所在区、县")
    private String county;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址", required = false)
    @Length(max = 50)
    private String address;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @Length(max = 50)
    private String lng;
    /**
     * 维度
     */
    @ApiModelProperty(value = "纬度")
    @Length(max = 50)
    private String lat;
    /**
     * 个性签名
     */
    @ApiModelProperty(value = "个性签名", required = false)
    private String perSign;
}
