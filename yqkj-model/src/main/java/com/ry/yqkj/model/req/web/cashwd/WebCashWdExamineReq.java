package com.ry.yqkj.model.req.web.cashwd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 提现审批信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebCashWdExamineReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键ID
     */
    @NotNull(message = "记录ID不能为空")
    private Long cashId;
    /**
     * 状态
     */
    @NotBlank(message = "审核状态必填")
    private String status;

    /**
     * 提现失败，不通过原因必填
     */
    private String remark;

    /**
     * 转账凭证
     */
    private String attach;


}
