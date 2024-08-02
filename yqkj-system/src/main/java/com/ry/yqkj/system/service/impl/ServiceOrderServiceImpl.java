package com.ry.yqkj.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.common.utils.uuid.SnowflakeIdUtil;
import com.ry.yqkj.model.enums.InviteStatusEnum;
import com.ry.yqkj.model.enums.ModulePreFixEnum;
import com.ry.yqkj.model.enums.OrderStatusEnum;
import com.ry.yqkj.model.enums.TradeStatusEnum;
import com.ry.yqkj.model.req.app.order.OrderCancelReq;
import com.ry.yqkj.model.req.app.order.OrderDoneReq;
import com.ry.yqkj.model.req.app.order.OrderInviteReq;
import com.ry.yqkj.model.req.app.order.OrderReq;
import com.ry.yqkj.model.resp.app.assist.AssistEvalResp;
import com.ry.yqkj.model.resp.app.assist.OrderPageReq;
import com.ry.yqkj.model.resp.app.cliuser.OrderEvalResp;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.app.order.OrderSimpleResp;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.component.WxPayComponent;
import com.ry.yqkj.system.domain.*;
import com.ry.yqkj.system.mapper.app.ServiceOrderMapper;
import com.ry.yqkj.system.service.*;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : lihy
 * @Description : 订单 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements IServiceOrderService {


    @Resource
    private IAssistantService assistantService;

    @Resource
    private ITradeService tradeService;

    @Resource
    private AssistComponent assistComponent;

    @Resource
    private IFundService fundService;

    @Resource
    private WxPayComponent wxPayComponent;

    @Resource
    private IWxUserService wxUserService;

    @Resource
    private IOrderEvalService orderEvalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void order(OrderReq orderReq) {
        Long cliUserId = WxUserUtils.current().getUserId();

        //判断用户是否是助教、如果是不能下单是自己
        LambdaQueryWrapper<Assistant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assistant::getCliUserId, cliUserId);
        Assistant myAssist = assistantService.getOne(wrapper);

        boolean isAssist = myAssist != null;

        List<Assistant> assistantList = assistantService.listByIds(orderReq.getAssistIdSet());
        for (Assistant assistant : assistantList) {
            if (isAssist && ObjectUtil.equal(assistant.getId(), myAssist.getId())) {
                throw new ServiceException("您不能给自己的订单噢！");
            }
            //判断助教该时间段是否被预约
            BigDecimal price = assistant.getPrice();
            BigDecimal reserveDur = orderReq.getReserveDur();
            //助教费用
            BigDecimal assistTotal = price.multiply(reserveDur);
            //总费用
            ServiceOrder serviceOrder = DozerUtil.map(orderReq, ServiceOrder.class);
            serviceOrder.setCliUserId(cliUserId);
            serviceOrder.setAssistId(assistant.getId());
            serviceOrder.setOrderNo(ModulePreFixEnum.SERVICE_ORDER.code + SnowflakeIdUtil.nextId());
            serviceOrder.setStatus(OrderStatusEnum.ORDER.code);
            serviceOrder.setInviteStatus(InviteStatusEnum.INVITED.code);
            serviceOrder.setAssistFee(assistTotal);
            serviceOrder.setPrice(price);
            serviceOrder.setTotalAmount(assistTotal);

            if (serviceOrder.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("订单金额异常！");
            }

            //预约时间大于当前时间
            if (orderReq.getReserveTime().isBefore(LocalDateTime.now())) {
                throw new ServiceException("预约时间必须大于当前时间！");
            }
            serviceOrder.setCategory("助教订单");
            this.baseMapper.insert(serviceOrder);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(OrderCancelReq orderCancelReq) {
        ServiceOrder serviceOrder = this.getCurrentUserServiceOrder(orderCancelReq.getOrderNo());
        if (ObjectUtil.notEqual(serviceOrder.getStatus(), OrderStatusEnum.ORDER.code)) {
            throw new ServiceException("状态异常，不允许操作！");
        }
        serviceOrder.setCancelFrom("U");
        serviceOrder.setStatus(OrderStatusEnum.CANCELED.code);
        serviceOrder.setInviteStatus(InviteStatusEnum.REFUSED.code);
        serviceOrder.setRemark(orderCancelReq.getRemark());
        updateById(serviceOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrepayWithRequestPaymentResponse preparePay(String orderNo) {
        ServiceOrder serviceOrder = this.getCurrentUserServiceOrder(orderNo);
        if (ObjectUtil.notEqual(OrderStatusEnum.ORDER.code, serviceOrder.getStatus())) {
            throw new ServiceException("状态异常！");
        }
        //获取支付的openId
        Long cliUserId = serviceOrder.getCliUserId();
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getCliUserId, cliUserId);
        WxUser wxUser = wxUserService.getOne(wrapper);
        if (wxUser == null) {
            log.error("未获取到对应的openId. orderNo:{}", orderNo);
            throw new ServiceException("未获取到对应的openId！");
        }
        //创建交易
        Trade trade = tradeService.getByBizNo(serviceOrder.getOrderNo());
        if (trade == null) {
            trade = new Trade();
            //生成交易记录
            trade.setAccountId(serviceOrder.getCliUserId());
            trade.setBizNo(serviceOrder.getOrderNo());
            trade.setAmount(serviceOrder.getTotalAmount());
            trade.setTradeNo(ModulePreFixEnum.TRADE.code + SnowflakeIdUtil.nextId());
            trade.setStatus(TradeStatusEnum.PROCESSING.code);
            tradeService.save(trade);
        }
        return wxPayComponent.prepayWithRequestPayment(serviceOrder, wxUser.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payNotify(String orderNo, String tradeNo) {

        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getOrderNo, orderNo);
        ServiceOrder serviceOrder = this.getOne(wrapper);
        if (serviceOrder == null) {
            log.error("serviceOrderService#payDoneCallBack order not found ! orderNo:{}", orderNo);
            throw new ServiceException("找不到对应的订单");
        }
        if (ObjectUtil.equal(serviceOrder.getStatus(), OrderStatusEnum.PAID.code)) {
            //幂等
            return;
        }
        if (ObjectUtil.notEqual(serviceOrder.getStatus(), OrderStatusEnum.ORDER.code)) {
            log.error("订单状态异常 ！");
            return;
        }
        serviceOrder.setStatus(OrderStatusEnum.PAID.code);
        this.baseMapper.updateById(serviceOrder);

        Trade trade = tradeService.getByBizNo(serviceOrder.getOrderNo());
        if (trade == null) {
            log.error("trade get fail! orderNo:{}", orderNo);
            throw new ServiceException("无法获取到对应的交易信息！");
        }
        trade.setStatus(TradeStatusEnum.DONE.code);
        tradeService.updateById(trade);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void done(OrderDoneReq orderDoneReq) {
        ServiceOrder serviceOrder = this.getCurrentUserServiceOrder(orderDoneReq.getOrderNo());
        if (ObjectUtil.notEqual(OrderStatusEnum.SERVICING.code, serviceOrder.getStatus())) {
            throw new ServiceException("状态异常！");
        }
        //TODO 验证订单完成的时间（跟服务时间应该有一定的间隔）
        serviceOrder.setStatus(OrderStatusEnum.DONE.code);
        updateById(serviceOrder);
        synchronized (serviceOrder.getCliUserId()) {
            //TODO 订单完成后、订单收入金额 -> 可提现金额（提现设置7天可提现一次）
            Fund fund = fundService.createFund(serviceOrder.getAssistId());
            //冻结金额
            BigDecimal freezeAmount = fund.getFreezeAmount();
            //可提现金额
            BigDecimal withdrawAmount = fund.getWithdrawAmount();
            //TODO 后期修改通过提成规则获取比例
            //本次入账金额
            BigDecimal income = serviceOrder.getTotalAmount().multiply(serviceOrder.getRate());
            freezeAmount = freezeAmount.subtract(income);
            withdrawAmount = withdrawAmount.add(income);
            fund.setFreezeAmount(freezeAmount);
            fund.setWithdrawAmount(withdrawAmount);
            fundService.updateById(fund);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invite(OrderInviteReq inviteReq) {
        Assistant assistant = assistComponent.currentUserToAssistant();
        if (assistant == null) {
            log.error("invite failed! userId={}的用户无助教身份！", WxUserUtils.current().getUserId());
            throw new ServiceException("无法操作！");
        }
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getOrderNo, inviteReq.getOrderNo());
        wrapper.eq(ServiceOrder::getAssistId, assistant.getId());
        ServiceOrder serviceOrder = this.baseMapper.selectOne(wrapper);
        if (serviceOrder == null) {
            log.error("service not found ！orderNo={}！", inviteReq.getOrderNo());
            throw new ServiceException("无法操作！");
        }
        if (ObjectUtil.notEqual(InviteStatusEnum.INVITED.code, serviceOrder.getInviteStatus())) {
            throw new ServiceException("状态异常，无法操作！");
        }

        if (!(ObjectUtil.equal(InviteStatusEnum.REFUSED.code, inviteReq.getInviteStatus())
                || ObjectUtil.equal(InviteStatusEnum.RECEIVED.code, inviteReq.getInviteStatus()))) {
            throw new ServiceException("参数异常，无法操作！");
        }

        if (ObjectUtil.equal(InviteStatusEnum.REFUSED.code, inviteReq.getInviteStatus())) {
            if (StringUtils.isBlank(inviteReq.getRefusedReason())) {
                throw new ServiceException("请填写拒绝原因！");
            }
            serviceOrder.setRefuseReason(inviteReq.getRefusedReason());
            serviceOrder.setCancelFrom("A");
            //拒绝邀约，取消订单
            serviceOrder.setStatus(OrderStatusEnum.CANCELED.code);
            //获取订单交易记录
//            Trade trade = tradeService.getByBizNo(serviceOrder.getOrderNo());
//            trade.setStatus(TradeStatusEnum.EXPIRED.code);
//            trade.setRemark("订单取消");
//            tradeService.updateById(trade);
        }
        serviceOrder.setInviteStatus(inviteReq.getInviteStatus());
        updateById(serviceOrder);
    }

    @Override
    public OrderDetailResp assistOrderDetail(String orderNo) {
        Assistant assistant = assistComponent.currentUserToAssistant();
        if (assistant == null) {
            throw new ServiceException("无法获取订单！");
        }
        OrderDetailResp orderDetailResp = this.baseMapper.detail(orderNo);
        if (ObjectUtil.notEqual(assistant.getId(), orderDetailResp.getAssistId())) {
            throw new ServiceException("无法获取订单！");
        }
        return orderDetailResp;
    }


    @Override
    public OrderDetailResp cliUserOrderDetail(String orderNo) {
        WxAppUser wxAppUser = WxUserUtils.current();
        OrderDetailResp orderDetailResp = this.baseMapper.detail(orderNo);
        if (ObjectUtil.notEqual(wxAppUser.getUserId(), orderDetailResp.getCliUserId())) {
            throw new ServiceException("无法获取订单！");
        }
        if (ObjectUtil.equal(orderDetailResp.getStatus(), OrderStatusEnum.DONE.code)) {
            OrderEvalResp orderEval = orderEvalService.getDetail(orderDetailResp.getId());
            String tag = orderEval.getTag();
            if (StringUtils.isNoneBlank(tag)) {
                orderEval.setTags(Arrays.asList(StringUtils.split(tag, "、")));
            }
            orderDetailResp.setOrderEval(orderEval);
        }
        return orderDetailResp;
    }


    @Override
    public PageResDomain<OrderSimpleResp> cliUserOrderPage(OrderPageReq orderPageReq) {
        Long cliUserId = WxUserUtils.current().getUserId();
        if (cliUserId == null) {
            return new PageResDomain<>(Lists.newArrayList(), 0L, orderPageReq.getPageSize(), orderPageReq.getCurrent());
        }
        orderPageReq.setCliUserId(cliUserId);
        return this.selectPage(orderPageReq);
    }

    @Override
    public PageResDomain<OrderSimpleResp> assistOrderPage(OrderPageReq orderPageReq) {
        Assistant assistant = assistComponent.currentUserToAssistant();
        if (assistant == null) {
            return new PageResDomain<>(Lists.newArrayList(), 0L, orderPageReq.getPageSize(), orderPageReq.getCurrent());
        }
        orderPageReq.setAssistId(assistant.getId());
        return this.selectPage(orderPageReq);
    }

    private PageResDomain<OrderSimpleResp> selectPage(OrderPageReq orderPageReq) {
        QueryWrapper<ServiceOrder> wrapper = SearchTool.invoke(orderPageReq);
        Page<OrderSimpleResp> page = new Page<>(orderPageReq.getCurrent(), orderPageReq.getPageSize());
        wrapper.orderByDesc("so.create_time");
        page = this.baseMapper.selectPage(page, wrapper);
        if (page.getTotal() <= 0) {
            return new PageResDomain<>(Lists.newArrayList(), page.getTotal(), page.getSize(), page.getCurrent());
        }
        renderServiceOrder(page.getRecords());
        return PageResDomain.parse(page, OrderSimpleResp.class);
    }

    private void renderServiceOrder(List<OrderSimpleResp> serviceOrders) {
        Set<Long> assistSet = serviceOrders.stream().map(OrderSimpleResp::getAssistId).collect(Collectors.toSet());
        Map<Long, AssistEvalResp> map = orderEvalService.assistEvalPage(assistSet);
        serviceOrders.forEach(order -> {
            AssistEvalResp assistEvalResp = map.get(order.getAssistId());
            if (assistEvalResp == null) {
                return;
            }
            OrderSimpleResp.AssistEval assistEval = new OrderSimpleResp.AssistEval();
            assistEval.setScore(assistEvalResp.getScore() == null ? BigDecimal.ZERO : assistEvalResp.getScore());
            if (StringUtils.isNoneBlank(assistEvalResp.getTag())) {
                assistEval.setTags(Arrays.asList(StringUtils.split(assistEvalResp.getTag(), "、")));
            }
        });
    }

    private ServiceOrder getCurrentUserServiceOrder(String orderNo) {

        WxAppUser wxAppUser = WxUserUtils.current();
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getOrderNo, orderNo);
        wrapper.eq(ServiceOrder::getCliUserId, wxAppUser.getUserId());
        ServiceOrder serviceOrder = this.baseMapper.selectOne(wrapper);
        if (serviceOrder == null) {
            log.error("getCurrentUserServiceOrder not found ! userId={},orderNo={}", wxAppUser.getUserId(), orderNo);
            throw new ServiceException("获取不到对应的订单！");
        }
        return serviceOrder;
    }

    private ServiceOrder getCurrentAssistServiceOrder(String orderNo) {
        Assistant assistant = assistComponent.currentUserToAssistant();
        if (assistant == null) {
            throw new ServiceException("非助教身份，无法获取！");
        }
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getOrderNo, orderNo);
        wrapper.eq(ServiceOrder::getAssistId, assistant.getId());
        ServiceOrder serviceOrder = this.baseMapper.selectOne(wrapper);
        if (serviceOrder == null) {
            log.error("getCurrentAssistServiceOrder not found ! assistId={},orderNo={}", assistant.getId(), orderNo);
            throw new ServiceException("获取不到对应的订单！");
        }
        return serviceOrder;
    }
}
