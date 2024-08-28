package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.cashwd.CashWdApplyReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.system.service.ICashWdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/cashwd/apply")
    @ApiOperation("提现申请")
    public R<Void> apply(@Validated @RequestBody CashWdApplyReq req) {
        return R.ok();
    }

    @PostMapping("/cashwd/page")
    @ApiOperation("提现记录")
    public R<PageResDomain<CashWdInfoResp>> page(@Validated @RequestBody CashWdPageReq req) {
        return R.ok();
    }
}
