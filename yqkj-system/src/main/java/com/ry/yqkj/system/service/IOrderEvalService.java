package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.cliuser.EvalPageRequest;
import com.ry.yqkj.model.req.app.cliuser.EvalRequest;
import com.ry.yqkj.model.resp.app.assist.AssistEvalResp;
import com.ry.yqkj.model.resp.app.cliuser.OrderEvalResp;
import com.ry.yqkj.system.domain.OrderEval;

import java.util.Map;
import java.util.Set;

/**
 * @author : lihy
 * @Description : 评价
 * @date : 2024/5/19 11:14 下午
 */
public interface IOrderEvalService extends IService<OrderEval> {


    /**
     * 评价
     *
     * @param evalRequest 评价参数
     */
    void eval(EvalRequest evalRequest);


    /**
     * 评价详情
     *
     * @param orderId 订单ID
     */
    OrderEvalResp getDetail(Long orderId);


    /**
     * 助教评价列表
     *
     * @param request 助教ID
     */
    PageResDomain<OrderEvalResp> assistEvalPage(EvalPageRequest request);


    /**
     * 助教评价信息
     *
     * @param assistIdSet 助教ID
     */
    Map<Long, AssistEvalResp> assistEvalPage(Set<Long> assistIdSet);


}
