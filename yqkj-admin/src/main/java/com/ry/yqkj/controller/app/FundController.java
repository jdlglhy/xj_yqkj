package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.model.resp.app.fund.FundInfoResp;
import com.ry.yqkj.system.domain.Fund;
import com.ry.yqkj.system.service.IFundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 前台-资金管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("前台-资金管理")
public class FundController extends WxBaseController {
    @Resource
    private IFundService fundService;

    @GetMapping("/fund_info")
    @ApiOperation("资金信息")
    public R<FundInfoResp> currentUserFundInfo() {
        Fund fund = fundService.createFund(WxUserUtils.current().getUserId());
        return R.ok(DozerUtil.map(fund, FundInfoResp.class));
    }
}
