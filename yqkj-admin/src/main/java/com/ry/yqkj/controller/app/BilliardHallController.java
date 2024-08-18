package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.app.billiardhall.HallApplyRequest;
import com.ry.yqkj.model.resp.app.billiardhall.AppBilliardHallInfoResp;
import com.ry.yqkj.system.domain.BilliardHall;
import com.ry.yqkj.system.service.IBilliardHallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 球馆controller
 * @date : 2024/7/8 12:11 上午
 */
@RestController
@Api("前台球馆管理")
public class BilliardHallController extends WxBaseController {

    @Resource
    private IBilliardHallService billiardHallService;

    @PostMapping("/billiard_hall/apply")
    @ApiOperation("申请")
    public R<Void> apply(@Validated @RequestBody HallApplyRequest request) {
        billiardHallService.apply(request);
        return R.ok();
    }

    @GetMapping("/billiard_hall/info")
    @ApiOperation("详情")
    public R<AppBilliardHallInfoResp> info() {
        BilliardHall billiardHall = billiardHallService.info();
        if (billiardHall != null) {
            return R.ok(DozerUtil.map(billiardHall, AppBilliardHallInfoResp.class));
        }
        return R.ok();
    }
}
