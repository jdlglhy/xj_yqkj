package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.system.domain.Trade;

/**
 * @author : lihy
 * @Description : 交易 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface ITradeService extends IService<Trade> {


    Trade getByBizNo(String bizNo);

}
