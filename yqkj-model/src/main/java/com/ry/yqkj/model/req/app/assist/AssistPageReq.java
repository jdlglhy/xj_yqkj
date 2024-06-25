package com.ry.yqkj.model.req.app.assist;

import com.ry.yqkj.common.core.page.PageReqDomain;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 助教列表请求参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistPageReq extends PageReqDomain {
    /**
     * 昵称
     */
    @ApiModelProperty(value = "宝贝昵称", required = false)
    @Search(type = SearchType.LIKE)
    private String nickName;
    /**
     * 省
     */
    @ApiModelProperty(value = "省", required = false)
    @Search
    private String province;
    /**
     * 市、区
     */
    @ApiModelProperty(value = "市", required = false)
    @Search
    private String city;

    /**
     * 区、县
     */
    @ApiModelProperty(value = "区、县", required = false)
    @Search
    private String county;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", required = false)
    @Search
    private Integer sex;
    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;
}
