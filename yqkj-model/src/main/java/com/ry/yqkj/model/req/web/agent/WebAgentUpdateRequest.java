package com.ry.yqkj.model.req.web.agent;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 区域代理信息修改
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebAgentUpdateRequest implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * ID
     */
    @NotNull(message = "ID必填")
    private Long id;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 代理人邮箱(接收代理申请信息)
     */
    @Email(message = "电子邮件格式错误！")
    private String email;
}
