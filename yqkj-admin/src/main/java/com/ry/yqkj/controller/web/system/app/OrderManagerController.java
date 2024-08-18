package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
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
 * @Description : 后台-订单管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController("/service_order")
@Api("后台订单管理")
public class OrderManagerController extends BaseController {

    @Resource
    private IServiceOrderService serviceOrderService;

    @PostMapping("/page")
    @ApiOperation("订单列表")
    public R<PageResDomain<OrderSimpleResp>> page(@Validated @RequestBody OrderPageReq orderPageReq) {
        return R.ok(serviceOrderService.assistOrderPage(orderPageReq));
    }

    @GetMapping("/order_detail/{orderNo}")
    @ApiOperation("订单详情")
    public R<OrderDetailResp> detail(@PathVariable("orderNo") String orderNo) {
        return R.ok(serviceOrderService.assistOrderDetail(orderNo));
    }
}
