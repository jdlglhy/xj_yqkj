package com.ry.yqkj.common.core.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
@Data
public class PageReqDomain implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 当前页 默认为1
     */
    private Long current = 1L;

    /**
     * 分页大小 默认为10
     */
    private Long pageSize = 10L;

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;


    /**
     * 搜索值
     */
    @JsonIgnore
    private String searchValue;

}
