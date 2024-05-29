package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.model.req.assist.AssistApplyReq;
import com.ry.yqkj.model.resp.assist.AssistFormInfoResp;
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
    AssistFormInfoResp getLatestForm();


    /**
     * 审批
     *
     * @param formId
     * @param approveState
     * @param reason
     */
    void examine(Long formId,String approveState,String reason);


}
