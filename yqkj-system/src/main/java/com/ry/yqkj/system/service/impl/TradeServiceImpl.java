package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.system.domain.Trade;
import com.ry.yqkj.system.mapper.TradeMapper;
import com.ry.yqkj.system.service.ITradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : lihy
 * @Description : 交易 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class TradeServiceImpl extends ServiceImpl<TradeMapper, Trade> implements ITradeService {


    @Override
    public Trade getByBizNo(String bizNo) {

        LambdaQueryWrapper<Trade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trade::getBizNo, bizNo);
        return this.baseMapper.selectOne(wrapper);
    }
}
