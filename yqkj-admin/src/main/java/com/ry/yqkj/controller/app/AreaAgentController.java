package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.model.req.app.agent.AreaAgentApplyRequest;
import com.ry.yqkj.model.resp.web.agent.AreaAgentResp;
import com.ry.yqkj.system.service.IAreaAgentService;
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
 * @Description : 区域代理
 * @date : 2024/7/8 12:11 上午
 */
@RestController
@Api("前台区域代理")
public class AreaAgentController extends WxBaseController {

    @Resource
    private IAreaAgentService areaAgentService;

    @PostMapping("/area_agent/apply")
    @ApiOperation("申请")
    public R<Void> apply(@Validated @RequestBody AreaAgentApplyRequest request) {
        areaAgentService.apply(request);
        return R.ok();
    }

    @GetMapping("/area_agent/detail")
    @ApiOperation("详情")
    public R<AreaAgentResp> detail() {
        return R.ok(areaAgentService.detail());
    }
}
