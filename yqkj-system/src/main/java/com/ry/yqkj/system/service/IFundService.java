package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.system.domain.Fund;

/**
 * @author : lihy
 * @Description : 资金 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IFundService extends IService<Fund> {


    /**
     * 创建资金管理
     *
     * @param accountId
     * @return
     */
    Fund createFund(Long accountId);

}
