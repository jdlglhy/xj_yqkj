package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.req.app.cashwd.CashWdApplyReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.domain.CashWithdraw;
import com.ry.yqkj.system.domain.Fund;
import com.ry.yqkj.system.mapper.CashWdMapper;
import com.ry.yqkj.system.service.ICashWdService;
import com.ry.yqkj.system.service.IFundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 提现 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class CashWdServiceImpl extends ServiceImpl<CashWdMapper, CashWithdraw> implements ICashWdService {


    @Resource
    private AssistComponent assistComponent;
    @Resource
    private IFundService fundService;
    @Resource
    private CashWdMapper cashWdMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cashWdApply(CashWdApplyReq req) {
        Long userId = WxUserUtils.current().getUserId();
        Assistant assistant = assistComponent.getAssistant(userId);
        if (assistant == null) {
            throw new ServiceException("无法提现！");
        }
        //验证用户金额
        Fund fund = fundService.createFund(assistant.getId());
        if (fund.getWithdrawAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("可提现余额不足！");
        }
        if (fund.getWithdrawAmount().compareTo(req.getAmount()) <= 0) {
            throw new ServiceException("可提现余额不足！");
        }
        synchronized (fund) {
            //可提现金额 = 可提现金额 - 提现金额；
            //冻结金额 = 冻结金额 + 提现金额
            fund.setWithdrawAmount(fund.getWithdrawAmount().subtract(req.getAmount()));
            fund.setFreezeAmount(fund.getFreezeAmount().add(req.getAmount()));
            fundService.updateById(fund);
        }
        //提现记录
        CashWithdraw cashWithdraw = DozerUtil.map(req, CashWithdraw.class);
        cashWithdraw.setCreateBy("助教_" + assistant.getId());
        cashWithdraw.setCreateTime(new Date());
        cashWithdraw.setStatus(ApproveEnum.APPROVING.code);
        cashWithdraw.setAccountId(assistant.getId());
        this.save(cashWithdraw);

    }

    @Override
    public PageResDomain<CashWdInfoResp> cashPageRecord(CashWdPageReq cashWdPageReq) {
        Page<CashWithdraw> page = new Page<>(cashWdPageReq.getCurrent(), cashWdPageReq.getPageSize());
        QueryWrapper<CashWithdraw> queryWrapper = SearchTool.invoke(cashWdPageReq);
        queryWrapper.lambda().orderByDesc(CashWithdraw::getAmount);
        page = cashWdMapper.selectPage(page, queryWrapper);
        return PageResDomain.parse(page, CashWdInfoResp.class);
    }
}
