package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.web.CommonExamReq;
import com.ry.yqkj.model.req.web.cashwd.WebCashWdPageReq;
import com.ry.yqkj.model.resp.app.order.OrderDetailResp;
import com.ry.yqkj.model.resp.web.cashwd.WebCashWdPageResp;
import com.ry.yqkj.system.manager.CashWdManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 后台-提现管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/cash_wd")
@Api("后台订提现管理")
public class CashWdManagerController extends BaseController {

    @Resource
    private CashWdManager cashWdManager;

    @PostMapping("/page")
    @ApiOperation("提现审核列表")
    public R<PageResDomain<WebCashWdPageResp>> page(@Validated @RequestBody WebCashWdPageReq webCashWdPageReq) {
        return R.ok(cashWdManager.page(webCashWdPageReq));
    }

    @PostMapping("/examine")
    @ApiOperation("提现审核")
    public R<OrderDetailResp> examine(@Validated @RequestBody CommonExamReq commonExamReq) {
        cashWdManager.examine(commonExamReq);
        return R.ok();
    }
}
