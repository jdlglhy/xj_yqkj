package com.ry.yqkj.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.SecurityUtils;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.common.utils.uuid.SnowflakeIdUtil;
import com.ry.yqkj.model.enums.ModulePreFixEnum;
import com.ry.yqkj.model.enums.TradeStatusEnum;
import com.ry.yqkj.model.req.app.cashwd.CashWdReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferBalanceResp;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.component.WxPayComponent;
import com.ry.yqkj.system.domain.CashWithdraw;
import com.ry.yqkj.system.domain.Fund;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.mapper.CashWdMapper;
import com.ry.yqkj.system.service.ICashWdService;
import com.ry.yqkj.system.service.IFundService;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private IWxUserService wxUserService;
    @Resource
    private AssistComponent assistComponent;
    @Resource
    private IFundService fundService;
    @Resource
    private CashWdMapper cashWdMapper;
    @Resource
    private WxPayComponent wxPayComponent;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferBalanceResp cashWdApply(CashWdReq req) throws Exception {
        Long cliUserId = WxUserUtils.current().getUserId();
        //验证用户金额
        Fund fund = fundService.createFund(cliUserId);
        if (fund.getWithdrawAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("可提现余额不足！");
        }
        if (fund.getWithdrawAmount().compareTo(req.getAmount()) < 0) {
            throw new ServiceException("可提现余额不足！");
        }
        //扣减用户可提现金额
        synchronized (fund) {
            //每次提现会将可提现金额转入到冻结金额
            fund.setWithdrawAmount(fund.getWithdrawAmount().subtract(req.getAmount()));
            fund.setTotalAmount(fund.getTotalAmount().subtract(req.getAmount()));
            fund.setModifyBy("系统_" + SecurityUtils.getUserId());
            fund.setModifyTime(new Date());
            fundService.updateById(fund);
        }
        //新增提现记录
        CashWithdraw cashWithdraw = DozerUtil.map(req, CashWithdraw.class);
        cashWithdraw.setWithdrawNo(ModulePreFixEnum.WITH_DRAW.code + SnowflakeIdUtil.nextId());
        cashWithdraw.setCreateBy("用户_" + cliUserId);
        cashWithdraw.setCreateTime(new Date());
        cashWithdraw.setStatus(TradeStatusEnum.PROCESSING.code);
        cashWithdraw.setAccountId(cliUserId);
        this.save(cashWithdraw);
        WxUser wxUser = wxUserService.getWxUserByCliUserId(cliUserId);
        //调用 商家转账到零钱接口
        TransferBalanceResp resp = wxPayComponent.transferToBalance(wxUser.getOpenId(), cashWithdraw.getWithdrawNo(), cashWithdraw.getAmount(), "用户提现");
        cashWithdraw.setPackageInfo(JSON.toJSONString(resp));
        cashWithdraw.setTransferBillNo(resp.getTransferBillNo());
        this.updateById(cashWithdraw);
        log.info("商家转账到零钱接口返回结果：cliUserId={},resp={}", cliUserId, resp);
        if (StringUtils.isBlank(resp.getPackageInfo())) {
            throw new ServiceException("提现失败！");
        }
        return resp;
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
