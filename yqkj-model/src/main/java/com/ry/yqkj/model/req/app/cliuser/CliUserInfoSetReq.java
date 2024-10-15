package com.ry.yqkj.model.req.app.cliuser;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 更新用户基本信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CliUserInfoSetReq implements Serializable {

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称",required = true)
    @NotBlank(message = "昵称不能为空")
    @Length(min = 2,max = 64)
    private String nickName;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像",required = false)
    private String avatar;

    /**
     * phone
     */
    @ApiModelProperty(value = "手机号",required = false)
    @Length(min = 2,max = 16)
    private String phone;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别",required = false)
    @Min(0)
    @Max(1)
    private Integer gender;

    /**
     * 个人简介
     */
    @ApiModelProperty(value = "个人简介",required = false)
    @Length(min = 1,max = 50)
    private String profile;




}
