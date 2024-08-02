package com.ry.yqkj.model.req.app.agent;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 区域代理申请
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AreaAgentApplyRequest implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 姓名
     */
    @NotBlank(message = "请填写姓名")
    @Length(max = 32)
    private String realName;
    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式")
    @Length(max = 16)
    private String contact;
    /**
     * 代理的城市
     */
    @Length(max = 12)
    @NotBlank(message = "区域名称")
    private String areaName;
    /**
     * 区域编码
     */
    @NotBlank(message = "区域编码")
    private String areaCode;

    /**
     * 代理人邮箱(接收代理申请信息)
     */
    private String email;
}
