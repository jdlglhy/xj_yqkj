package com.ry.yqkj.model.req.web.agent;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 审批
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AgentExamineRequest implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * ID
     */
    @NotNull(message = "ID必填")
    private Long id;
    /**
     * 状态
     */
    @NotBlank(message = "审批状态")
    private String status;
    /**
     * 拒绝原因
     */
    private String refuseReason;
}
