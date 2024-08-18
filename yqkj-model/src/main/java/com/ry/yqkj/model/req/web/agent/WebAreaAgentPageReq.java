package com.ry.yqkj.model.req.web.agent;

import com.ry.yqkj.common.core.page.PageReqDomain;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("区域代理分页请求参数")
public class WebAreaAgentPageReq extends PageReqDomain {
    private static final long serialVersionUID = -1L;

    /**
     * 联系方式
     */
    @Search(type = SearchType.LIKE)
    private String contact;

    /**
     * 姓名
     */
    @Search(type = SearchType.LIKE)
    private String realName;
    /**
     * 区域名称
     */
    @Search(type = SearchType.LIKE)
    private String areaName;

    /**
     * 区域名称
     */
    private String status;

}
