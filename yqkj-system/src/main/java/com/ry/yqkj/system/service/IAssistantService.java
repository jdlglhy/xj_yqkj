package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.assist.AssistApplyReq;
import com.ry.yqkj.model.req.app.assist.AssistPageReq;
import com.ry.yqkj.model.req.web.assist.AssistFormExamReq;
import com.ry.yqkj.model.resp.app.assist.AssistDetailResp;
import com.ry.yqkj.model.resp.app.assist.AssistFormInfoResp;
import com.ry.yqkj.model.resp.app.assist.AssistInfoResp;
import com.ry.yqkj.system.domain.Assistant;

/**
 * @author : lihy
 * @Description : 助教管理 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IAssistantService extends IService<Assistant> {


    /**
     * 申请
     *
     * @param assistApplyReq
     */
    void apply(AssistApplyReq assistApplyReq);


    /**
     * 获取最新单申请单信息
     *
     * @return
     */
    AssistFormInfoResp getLatestForm(Long cliUserId);


    /**
     * 审批
     *
     * @param req 审批请求参数
     */
    void examine(AssistFormExamReq req);

    /**
     * 助教分页
     *
     * @param assistPageReq
     * @return
     */
    PageResDomain<AssistInfoResp> assistPage(AssistPageReq assistPageReq);

    /**
     * 助教详情
     *
     * @param assistId
     * @return
     */
    AssistDetailResp assistDetail(Long assistId);

}
