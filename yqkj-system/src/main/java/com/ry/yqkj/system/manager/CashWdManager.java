package com.ry.yqkj.system.manager;

import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.web.CommonExamReq;
import com.ry.yqkj.model.req.web.cashwd.WebCashWdPageReq;
import com.ry.yqkj.model.resp.web.cashwd.WebCashWdPageResp;

/**
 * @author : lihy
 * @Description : 提现 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface CashWdManager {

    /**
     * 提现审核
     *
     * @param commonExamReq 转账请求参数
     */
    void examine(CommonExamReq commonExamReq);


    /**
     * 提现列表
     *
     * @param webCashWdPageReq 提现列表请求参数
     * @return 提现列表
     */
    PageResDomain<WebCashWdPageResp> page(WebCashWdPageReq webCashWdPageReq);
}
