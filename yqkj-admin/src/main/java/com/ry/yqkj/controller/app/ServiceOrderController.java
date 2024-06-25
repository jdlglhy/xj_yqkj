package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.annotation.RepeatSubmit;
import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.order.OrderInviteReq;
import com.ry.yqkj.model.resp.app.assist.OrderPageReq;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.app.order.OrderSimpleResp;
import com.ry.yqkj.system.service.IServiceOrderService;
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
@Api("前台助教侧订单管理")
public class ServiceOrderController extends WxBaseController {

    @Resource
    private IServiceOrderService serviceOrderService;

    @PostMapping("/assist/order/page")
    @ApiOperation("邀约分页列表")
    public R<PageResDomain<OrderSimpleResp>> assistOrderPage(@Validated @RequestBody OrderPageReq orderPageReq) {
        return R.ok(serviceOrderService.assistOrderPage(orderPageReq));
    }

    @GetMapping("/assist/order_detail/{orderNo}")
    @ApiOperation("邀约详情")
    public R<OrderDetailResp> assistOrderDetail(@PathVariable("orderNo")String orderNo) {
        return R.ok(serviceOrderService.assistOrderDetail(orderNo));
    }

    @PostMapping("/assist/order/invite")
    @ApiOperation("邀约处理 （接受、拒绝）")
    @RepeatSubmit
    public R<Void> invite(@Validated @RequestBody OrderInviteReq orderInviteReq) {
        serviceOrderService.invite(orderInviteReq);
        return R.ok();
    }
}
