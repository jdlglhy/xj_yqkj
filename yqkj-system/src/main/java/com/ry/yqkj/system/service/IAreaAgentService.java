package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.agent.AreaAgentApplyRequest;
import com.ry.yqkj.model.req.web.agent.AgentUpdateRequest;
import com.ry.yqkj.model.req.web.agent.AreaAgentPageReq;
import com.ry.yqkj.model.resp.web.agent.AreaAgentResp;
import com.ry.yqkj.system.domain.AreaAgent;

/**
 * @author : lihy
 * @Description : 区域代理 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IAreaAgentService extends IService<AreaAgent> {

    /**
     * 申请区域代理
     *
     * @param request
     */
    void apply(AreaAgentApplyRequest request);

    /**
     * 申请区域代理
     *
     * @param id     申请单ID
     * @param status 状态
     * @param reason 审批不通过原因
     */
    void examine(Long id, String status, String reason);


    /**
     * 变更代理信息
     *
     * @param request 变更参数
     */
    void updateAgent(AgentUpdateRequest request);


    /**
     * 区域代理分页列表数据
     *
     * @param req
     * @return PageResDomain
     */
    PageResDomain<AreaAgentResp> page(AreaAgentPageReq req);


    /**
     * 详情
     *
     * @return AreaAgentResp
     */
    AreaAgentResp detail();

}
