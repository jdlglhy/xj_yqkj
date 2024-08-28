package com.ry.yqkj.system.manager.impl;

import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.web.cashwd.WebCashWdExamineReq;
import com.ry.yqkj.model.req.web.cashwd.WebCashWdPageReq;
import com.ry.yqkj.model.resp.web.cashwd.WebCashWdPageResp;
import com.ry.yqkj.system.manager.CashWdManager;
import com.ry.yqkj.system.service.impl.CashWdServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 提现管理 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class CashWdManagerImpl implements CashWdManager {

    @Resource
    private CashWdServiceImpl cashWdService;


    @Override
    public void examine(WebCashWdExamineReq cashWdExamineReq) {

    }

    @Override
    public PageResDomain<WebCashWdPageResp> page(WebCashWdPageReq webCashWdPageReq) {
        return null;
    }
}
