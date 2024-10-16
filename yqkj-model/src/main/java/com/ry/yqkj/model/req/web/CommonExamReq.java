package com.ry.yqkj.model.req.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("审批请求参数")
public class CommonExamReq implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "审批单ID", required = true, notes = "")
    @NotNull(message = "审批单ID必填")
    private Long formId;

    @ApiModelProperty(value = "审批动作", required = true, notes = "approving=审批通过、refused=拒绝")
    @NotBlank(message = "审批动作不能为空")
    private String approveState;

    @ApiModelProperty(value = "审批意见", notes = "审批不通过时必填")
    private String reason;

    @ApiModelProperty(value = "附件信息", notes = "转账证明或者截图，审核通过必须提供转账截图")
    private List<String> attachList;
}
