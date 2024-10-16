package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.system.domain.Fund;
import com.ry.yqkj.system.mapper.FundMapper;
import com.ry.yqkj.system.service.IFundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 资金管理 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class FundServiceImpl extends ServiceImpl<FundMapper, Fund> implements IFundService {


    @Override
    public Fund createFund(Long accountId) {

        LambdaQueryWrapper<Fund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Fund::getAccountId, accountId);
        Fund fund = this.baseMapper.selectOne(wrapper);
        if (fund == null) {
            fund = new Fund();
            fund.setWithdrawAmount(BigDecimal.ZERO);
            fund.setFreezeAmount(BigDecimal.ZERO);
            fund.setTotalAmount(BigDecimal.ZERO);
            fund.setAccountId(accountId);
            save(fund);
        }
        return fund;
    }
}
