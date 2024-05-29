package com.ry.yqkj.model.req.assist;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 申请单请求参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistApplyReq implements Serializable {
    /**
     * 昵称
     */
    @ApiModelProperty(value = "宝贝昵称", required = false, notes = "不填写设置为微信昵称")
    @Length(max = 64)
    private String nickName;
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名必填")
    @Length(max = 12)
    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;
    /**
     * 费用
     */
    @ApiModelProperty(value = "费用", required = false, notes = "单位：元/小时")
    private BigDecimal price;
    /**
     * 身份证正面
     */
    @ApiModelProperty(value = "身份证正面（人面）", required = true)
    @NotBlank(message = "请上传身份证正面（人面）")
    private String idCardFront;
    /**
     * 身份证反面
     */
    @ApiModelProperty(value = "身份证反面（国徽面）", required = true)
    @NotBlank(message = "请上传身份证反面（国徽面）")
    private String idCardBack;
    /**
     * 生活照
     */
    @ApiModelProperty(value = "生活照（最多6张）", required = true)
    @NotBlank(message = "请上传生活照")
    private String liftPhotos;
    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", required = true)
    @NotBlank(message = "请上传高清头像")
    private String avatar;

    /**
     * 性别 0 = 男 1 = 女
     */
    @ApiModelProperty(value = "性别", notes = "0 = 男，1=女", required = true)
    @NotNull(message = "请选择性别")
    private Integer gender;

    /**
     * 手机号(验证码、自动获取微信手机号)
     */
    @ApiModelProperty(value = "手机号", required = true)
    @NotNull(message = "手机号必填")
    private String phone;
    /**
     * 微信
     */
    @ApiModelProperty(value = "微信号", required = false)
    private String wx;
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
     * 出生日期
     */
    @ApiModelProperty(value = "生日", required = false)
    private Date birthday;

    /**
     * 个性签名
     */
    @ApiModelProperty(value = "个性签名", required = false)
    private String perSign;
    /**
     * 推荐码
     */
    @ApiModelProperty(value = "推荐码", required = false)
    @Length(max = 200)
    private String recCode;
}
