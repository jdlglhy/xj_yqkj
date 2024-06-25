package com.ry.yqkj.system.mapper.app;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.app.order.OrderSimpleResp;
import com.ry.yqkj.system.domain.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : lihy
 * @Description : 订单 mapper
 * @date : 2024/5/19 11:32 下午
 */
@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {


    /**
     * 订单分页列表
     *
     * @param page
     * @param wrapper
     * @return
     */
    Page<OrderSimpleResp> selectPage(Page<OrderSimpleResp> page, @Param("ew") Wrapper<ServiceOrder> wrapper);


    /**
     * 订单详情
     *
     * @param orderNo
     * @return
     */
    OrderDetailResp detail(@Param("orderNo") String orderNo);

}
