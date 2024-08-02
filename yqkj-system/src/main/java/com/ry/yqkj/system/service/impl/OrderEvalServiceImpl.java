package com.ry.yqkj.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.ry.yqkj.common.constant.Constants;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.StringUtils;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.model.enums.OrderStatusEnum;
import com.ry.yqkj.model.req.app.cliuser.EvalPageRequest;
import com.ry.yqkj.model.req.app.cliuser.EvalRequest;
import com.ry.yqkj.model.resp.app.assist.AssistEvalResp;
import com.ry.yqkj.model.resp.app.cliuser.OrderEvalResp;
import com.ry.yqkj.system.domain.OrderEval;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.ry.yqkj.system.mapper.OrderEvalMapper;
import com.ry.yqkj.system.service.IOrderEvalService;
import com.ry.yqkj.system.service.IServiceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : lihy
 * @Description : 评价 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class OrderEvalServiceImpl extends ServiceImpl<OrderEvalMapper, OrderEval> implements IOrderEvalService {


    @Resource
    private IServiceOrderService serviceOrderService;

    @Override
    public void eval(EvalRequest evalRequest) {
        ServiceOrder serviceOrder = serviceOrderService.getById(evalRequest.getOrderId());
        if (serviceOrder == null) {
            throw new ServiceException("未找到对应订单！");
        }
        if (ObjectUtil.notEqual(OrderStatusEnum.DONE.code, serviceOrder.getStatus())) {
            throw new ServiceException("订单完成后才能评价哦！");
        }
        if (ObjectUtil.notEqual(serviceOrder.getCliUserId(), WxUserUtils.current().getUserId())) {
            throw new ServiceException("非法订单，无法评价！");
        }
        OrderEval orderEval = DozerUtil.map(evalRequest, OrderEval.class);
        orderEval.setAssistId(serviceOrder.getAssistId());
        orderEval.setCliUserId(serviceOrder.getCliUserId());
        if (CollUtil.isNotEmpty(evalRequest.getTags())) {
            orderEval.setTag(StringUtils.join(evalRequest.getTags(), "、"));
        }
        //总分值：满意（10），三个标签（15）= 25（0.2分比例）
        //满意 + 10、不满意 + 0
        BigDecimal totalScore = BigDecimal.ZERO;
        int score = Constants.TRUE.equals(evalRequest.getSatisfied()) ? 10 : 0;
        score = score + orderEval.getServiceAttitude() + orderEval.getSpeciality() + orderEval.getTimeLiness();
        totalScore = new BigDecimal(score).multiply(new BigDecimal("0.2"));
        orderEval.setScore(totalScore);
        this.baseMapper.insert(orderEval);
    }

    @Override
    public OrderEvalResp getDetail(Long orderId) {

        LambdaQueryWrapper<OrderEval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEval::getOrderId, orderId);
        OrderEval orderEval = baseMapper.selectOne(wrapper);
        if (orderEval == null) {
            return new OrderEvalResp();
        }
        return DozerUtil.map(orderEval, OrderEvalResp.class);
    }

    @Override
    public PageResDomain<OrderEvalResp> assistEvalPage(EvalPageRequest request) {

        LambdaQueryWrapper<OrderEval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEval::getAssistId, request.getAssistId());
        wrapper.orderByDesc(OrderEval::getId);
        Page<OrderEval> page = new Page<>(request.getCurrent(), request.getPageSize());
        page = baseMapper.selectPage(page, wrapper);
        if (page.getTotal() <= 0) {
            return new PageResDomain<>(Lists.newArrayList(), 0L, request.getPageSize(), request.getCurrent());
        }
        List<OrderEvalResp> orderEvalRespList = Lists.newArrayList();
        page.getRecords().forEach(eval -> {
            OrderEvalResp orderEvalResp = DozerUtil.map(eval, OrderEvalResp.class);
            if (StringUtils.isNoneBlank(eval.getTag())) {
                orderEvalResp.setTags(Arrays.asList(StringUtils.split(eval.getTag(), "、")));
            }
            orderEvalRespList.add(orderEvalResp);
        });
        return new PageResDomain<>(orderEvalRespList, page.getTotal(), request.getPageSize(), request.getCurrent());
    }

    @Override
    public Map<Long, AssistEvalResp> assistEvalPage(Set<Long> assistIdSet) {
        List<AssistEvalResp> assistEvalResps = baseMapper.selectAssistEval(assistIdSet);
        if (CollUtil.isEmpty(assistEvalResps)) {
            return Maps.newHashMap();
        }
        return assistEvalResps.stream().collect(Collectors.toMap(AssistEvalResp::getAssistId, Function.identity()));
    }

}
