package com.ry.yqkj.model.resp.app.assist;

import com.ry.yqkj.common.core.page.PageReqDomain;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import lombok.Data;

import java.time.LocalDateTime;
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
     * 订单状态 订单状态 state  已下单（ordered）、已取消（canceled）、已支付/待服务（paid）、服务中（servicing）、已完成（done）
     */
    @Search(type = SearchType.IN,tableAlias = "so")
    private List<String> statusList;
    /**
     * 邀约状态（W = 已邀约  A= 已接单 R = 拒绝（取消订单） ）
     */
    @Search(type = SearchType.IN,tableAlias = "so")
    private String inviteStatus;
}
