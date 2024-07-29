package com.ry.yqkj.model.req.app.cliuser;

import com.ry.yqkj.common.core.page.PageReqDomain;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author : lihy
 * @Description : 评价列表
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class EvalPageRequest extends PageReqDomain {

    private static final long serialVersionUID = -1L;
    /**
     * 助教ID
     */
    @NotNull(message = "助教ID必填！")
    private Long assistId;
}
