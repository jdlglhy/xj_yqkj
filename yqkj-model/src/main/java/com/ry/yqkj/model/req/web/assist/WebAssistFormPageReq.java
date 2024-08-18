package com.ry.yqkj.model.req.web.assist;

import com.ry.yqkj.common.core.page.PageReqDomain;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("助教申请单请求参数")
public class WebAssistFormPageReq extends PageReqDomain {
    private static final long serialVersionUID = -1L;

}
