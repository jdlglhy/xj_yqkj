package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.annotation.RepeatSubmit;
import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferBalanceResp;
import com.ry.yqkj.system.service.ICashWdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 前台-提现管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("前台-提现管理")
public class CashWdController extends WxBaseController {


    @Resource
    private ICashWdService cashWdService;


    @PostMapping("/cash_wd/apply")
    @ApiOperation("提现申请")
    @RepeatSubmit(interval = 1000, message = "请勿重复操作！")
    public R<TransferBalanceResp> apply(@Validated @RequestBody CashWdReq req) {
        try {
            return R.ok(cashWdService.cashWdApply(req));
        } catch (Exception e) {
            logger.error("提现失败！",e);
            if(e instanceof ServiceException){
                ServiceException serviceException = (ServiceException) e;
                return R.fail(serviceException.getMessage());
            }
        }
        return R.ok();
    }

    @GetMapping("/cash_wd/user_confirm")
    @ApiOperation("获取用户确认收款参数")
    public R<TransferBalanceResp> userConfirm(@RequestParam("id") Long id) {
        return R.ok(cashWdService.getUserConfirmPackage(id));
    }

    @PostMapping("/cash_wd/page")
    @ApiOperation("提现记录")
    public R<PageResDomain<CashWdInfoResp>> page(@Validated @RequestBody CashWdPageReq req) {
        return R.ok(cashWdService.cashPageRecord(req));
    }
}
