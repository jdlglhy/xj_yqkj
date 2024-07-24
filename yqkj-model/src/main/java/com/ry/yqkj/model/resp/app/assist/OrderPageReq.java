package com.ry.yqkj.model.resp.app.assist;

import com.ry.yqkj.common.core.page.PageReqDomain;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import lombok.Data;

import java.util.List;

/**
 * @author : lihy
 * @Description : 订单分页查询
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderPageReq extends PageReqDomain {

    /**
     * 服务订单号
     */
    @Search(tableAlias = "so")
    private String orderNo;

    /**
     * 用户ID
     */
    @Search(tableAlias = "so")
    private Long cliUserId;
    /**
     * 助教ID
     */
    @Search(tableAlias = "so")
    private Long assistId;

    /**
     * 订单状态 订单状态 state  已下单（order）、已取消（canceled）、已支付/待服务（paid）、服务中（servicing）、已完成（done）
     */
    @Search(type = SearchType.IN, tableAlias = "so",field = "status")
    private List<String> statusList;
    /**
     * 邀约状态 invited = 已邀约、received=已接单、refused=已拒绝
     */
    @Search(type = SearchType.IN, tableAlias = "so")
    private String inviteStatus;
    /**
     * 评价状态 ： N = 未评价（默认） Y=已评价
     */
    @Search(type = SearchType.EQ, tableAlias = "so")
    private String evalStatus;
}
