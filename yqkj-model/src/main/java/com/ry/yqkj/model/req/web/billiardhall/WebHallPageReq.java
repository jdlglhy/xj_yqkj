package com.ry.yqkj.model.req.web.billiardhall;

import com.ry.yqkj.common.core.page.PageReqDomain;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("球馆分页请求参数")
public class WebHallPageReq extends PageReqDomain {
    private static final long serialVersionUID = -1L;

    /**
     * 球馆电话
     */
    private String hallName;
    /**
     * 负责人
     */
    private String hostName;

}
