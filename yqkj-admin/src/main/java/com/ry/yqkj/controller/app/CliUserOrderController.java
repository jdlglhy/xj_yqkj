package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.annotation.RepeatSubmit;
import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.order.OrderCancelReq;
import com.ry.yqkj.model.req.app.order.OrderDoneReq;
import com.ry.yqkj.model.req.app.order.OrderPrepareReq;
import com.ry.yqkj.model.req.app.order.OrderReq;
import com.ry.yqkj.model.resp.app.assist.OrderPageReq;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.app.order.OrderSimpleResp;
import com.ry.yqkj.system.service.IServiceOrderService;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 助教管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("前台用户侧订单管理")
public class CliUserOrderController extends WxBaseController {

    @Resource
    private IServiceOrderService serviceOrderService;

    @PostMapping("/cli_user/order/page")
    @ApiOperation("用户视角订单分页列表")
    public R<PageResDomain<OrderSimpleResp>> cliUserPage(@Validated @RequestBody OrderPageReq req) {
        return R.ok(serviceOrderService.cliUserOrderPage(req));
    }

    @GetMapping("/cli_user/order_detail/{orderNo}")
    @ApiOperation("订单详情")
    public R<OrderDetailResp> orderDetail(@PathVariable("orderNo") String orderNo) {
        return R.ok(serviceOrderService.cliUserOrderDetail(orderNo));
    }

    @PostMapping("/cli_user/order")
    @ApiOperation("下单")
    @RepeatSubmit
    public R<Void> order(@Validated @RequestBody OrderReq orderReq) {
        serviceOrderService.order(orderReq);
        return R.ok();
    }

    @PostMapping("/cli_user/cancel")
    @ApiOperation("取消")
    @RepeatSubmit
    public R<Void> canceled(@Validated @RequestBody OrderCancelReq orderCancelReq) {
        serviceOrderService.cancel(orderCancelReq);
        return R.ok();
    }

    @PostMapping("/cli_user/prepare")
    @ApiOperation("预支付")
    @RepeatSubmit
    public R<PrepayWithRequestPaymentResponse> prepare(@Validated @RequestBody OrderPrepareReq orderPrepareReq) {
        return R.ok(serviceOrderService.preparePay(orderPrepareReq.getOrderNo()));
    }

    @PostMapping("/cli_user/done")
    @ApiOperation("完成")
    @RepeatSubmit
    public R<Void> done(@Validated @RequestBody OrderDoneReq orderDoneReq) {
        serviceOrderService.done(orderDoneReq);
        return R.ok();
    }
}
