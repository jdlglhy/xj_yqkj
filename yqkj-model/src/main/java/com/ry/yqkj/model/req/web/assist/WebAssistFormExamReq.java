package com.ry.yqkj.model.req.web.assist;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("审批请求参数")
public class WebAssistFormExamReq implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "审批单ID", required = true, notes = "")
    @NotNull(message = "审批单ID必填")
    private Long formId;

    @ApiModelProperty(value = "审批动作", required = true, notes = "approving=审批通过、refused=拒绝")
    @NotBlank(message = "审批动作")
    private String approveState;

    @ApiModelProperty(value = "审批意见",notes = "审批不通过时必填")
    private String reason;
}
