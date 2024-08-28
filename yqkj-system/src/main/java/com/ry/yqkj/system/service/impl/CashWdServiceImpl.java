package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.cashwd.CashWdApplyReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.system.domain.CashWithdraw;
import com.ry.yqkj.system.mapper.CashWdMapper;
import com.ry.yqkj.system.service.ICashWdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : lihy
 * @Description : 提现 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class CashWdServiceImpl extends ServiceImpl<CashWdMapper, CashWithdraw> implements ICashWdService {


    @Override
    public void cashWdApply(CashWdApplyReq req) {

    }

    @Override
    public PageResDomain<CashWdInfoResp> cashPageRecord(CashWdPageReq page) {
        return null;
    }
}
