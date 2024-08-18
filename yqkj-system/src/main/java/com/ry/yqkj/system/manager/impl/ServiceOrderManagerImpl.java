package com.ry.yqkj.system.manager.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.ry.yqkj.system.manager.ServiceOrderManager;
import com.ry.yqkj.system.mapper.app.ServiceOrderMapper;
import com.ry.yqkj.system.service.IServiceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : lihy
 * @Description : 订单 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class ServiceOrderManagerImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderManager {


}
