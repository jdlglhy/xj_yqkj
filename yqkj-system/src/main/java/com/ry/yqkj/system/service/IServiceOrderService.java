package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.order.*;
import com.ry.yqkj.model.resp.app.assist.OrderPageReq;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.app.order.OrderSimpleResp;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;

/**
 * @author : lihy
 * @Description : 订单 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IServiceOrderService extends IService<ServiceOrder> {

    /**
     * 下单
     *
     * @param orderReq 下单请求参数
     */
    void order(OrderReq orderReq);

    /**
     * 取消下单
     *
     * @param orderCancelReq 取消下单参数
     */
    void cancel(OrderCancelReq orderCancelReq);

    /**
     * 预支付
     *
     * @param orderNo 支付订单参数
     */
    PrepayWithRequestPaymentResponse preparePay(String orderNo);

    /**
     * 支付成功回调
     *
     * @param tradeNo 第三方交易号
     */
    void payNotify(String orderNum, String tradeNo);

    /**
     * 完成订单（订单完成后该笔订单分佣进入冻结金额，待完成三天后转入可提现金额）
     *
     * @param orderDoneReq 支付订单参数
     */
    void done(OrderDoneReq orderDoneReq);


    /**
     * 处理邀请订单
     *
     * @param inviteReq 邀约参数
     */
    void invite(OrderInviteReq inviteReq);

    /**
     * 服务开始
     *
     * @param serviceStartReq 服务开始参数
     */
    void orderServiceStart(OrderServiceStartReq serviceStartReq);

    /**
     * 助教获取订单详情
     *
     * @return 订单详情
     */
    OrderDetailResp assistOrderDetail(String orderNo);


    /**
     * 用户获取订单详情
     *
     * @return 订单详情
     */
    OrderDetailResp cliUserOrderDetail(String orderNo);

    /**
     * 用户维度订单
     *
     * @return 订单列表
     */
    PageResDomain<OrderSimpleResp> cliUserOrderPage(OrderPageReq orderPageReq);

    /**
     * 助教维度订单
     *
     * @return 订单列表
     */
    PageResDomain<OrderSimpleResp> assistOrderPage(OrderPageReq orderPageReq);


    /**
     * 通过订单号获取
     *
     * @param orderNo
     * @return
     */
    ServiceOrder getByOrderNo(String orderNo);
}
