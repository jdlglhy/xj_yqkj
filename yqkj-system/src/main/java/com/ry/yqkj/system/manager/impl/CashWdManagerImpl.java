package com.ry.yqkj.system.manager.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.SecurityUtils;
import com.ry.yqkj.common.utils.StringUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.req.web.CommonExamReq;
import com.ry.yqkj.model.req.web.cashwd.WebCashWdPageReq;
import com.ry.yqkj.model.resp.web.cashwd.WebCashWdPageResp;
import com.ry.yqkj.system.domain.CashWithdraw;
import com.ry.yqkj.system.domain.Fund;
import com.ry.yqkj.system.manager.CashWdManager;
import com.ry.yqkj.system.mapper.CashWdMapper;
import com.ry.yqkj.system.service.IFundService;
import com.ry.yqkj.system.service.impl.CashWdServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

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
    @Resource
    private CashWdMapper cashWdMapper;
    @Resource
    private IFundService fundService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(CommonExamReq req) {
        CashWithdraw cashWithdraw = cashWdService.getById(req.getFormId());
        if (cashWithdraw == null) {
            throw new ServiceException("数据不存在！");
        }
        if (ObjectUtil.notEqual(ApproveEnum.APPROVING.code, cashWithdraw.getStatus())) {
            throw new ServiceException("状态不可操作！");
        }
        //审批不通过、回退冻结 到 可提现余额
        if (ObjectUtil.equal(req.getApproveState(), ApproveEnum.REFUSED.code)) {
            if (StringUtils.isBlank(req.getReason())) {
                throw new ServiceException("请填写原因！");
            }
            cashWithdraw.setRemark(req.getReason());
            cashWithdraw.setStatus(ApproveEnum.REFUSED.code);
            cashWithdraw.setModifyBy("系统_" + SecurityUtils.getUserId());
            cashWithdraw.setModifyTime(new Date());
            if (StringUtils.isNotEmpty(req.getAttachList())) {
                cashWithdraw.setAttach(StringUtils.join(req.getAttachList(), ","));
            }
            cashWdService.updateById(cashWithdraw);

            //更新账户余额
            Fund fund = fundService.createFund(cashWithdraw.getAccountId());
            synchronized (fund) {
                fund.setWithdrawAmount(fund.getWithdrawAmount().add(cashWithdraw.getAmount()));
                fund.setFreezeAmount(fund.getFreezeAmount().subtract(cashWithdraw.getAmount()));
                fund.setModifyBy("系统_" + SecurityUtils.getUserId());
                fund.setModifyTime(new Date());
                fundService.updateById(fund);
            }
            return;
        }
        //通过，扣除冻结
        if (ObjectUtil.equal(req.getApproveState(), ApproveEnum.APPROVED.code)) {
            if (CollUtil.isEmpty(req.getAttachList())) {
                throw new ServiceException("请上传交易凭证！");
            }
            cashWithdraw.setStatus(ApproveEnum.APPROVED.code);
            cashWithdraw.setModifyBy("系统_" + SecurityUtils.getUserId());
            cashWithdraw.setModifyTime(new Date());
            cashWithdraw.setAttach(StringUtils.join(req.getAttachList(), ","));
            cashWdService.updateById(cashWithdraw);
            //更新账户余额
            Fund fund = fundService.createFund(cashWithdraw.getAccountId());
            synchronized (fund) {
                fund.setFreezeAmount(fund.getFreezeAmount().subtract(cashWithdraw.getAmount()));
                fund.setModifyBy("系统_" + SecurityUtils.getUserId());
                fund.setModifyTime(new Date());
                fundService.updateById(fund);
            }
        }
    }

    @Override
    public PageResDomain<WebCashWdPageResp> page(WebCashWdPageReq webCashWdPageReq) {
        Page<CashWithdraw> page = new Page<>(webCashWdPageReq.getCurrent(), webCashWdPageReq.getPageSize());
        QueryWrapper<CashWithdraw> queryWrapper = SearchTool.invoke(webCashWdPageReq);
        queryWrapper.lambda().orderByDesc(CashWithdraw::getCreateTime);
        page = cashWdMapper.selectPage(page, queryWrapper);
        return PageResDomain.parse(page, WebCashWdPageResp.class);
    }
}
