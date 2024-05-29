package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.model.req.assist.AssistApplyReq;
import com.ry.yqkj.model.resp.assist.AssistFormInfoResp;
import com.ry.yqkj.system.service.IAssistantService;
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
 * @Description : 助教管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("助教管理控制器")
public class AssistController extends WxBaseController {

    @Resource
    private IAssistantService assistantService;

    @PostMapping("/assist/form")
    @ApiOperation("申请")
    public R<Void> apply(@Validated @RequestBody AssistApplyReq req) {
        assistantService.apply(req);
        return R.ok();
    }

    @GetMapping("/assist/form/get")
    @ApiOperation("获取当前用户的申请信息")
    public R<AssistFormInfoResp> assistFormInfo() {
        AssistFormInfoResp resp = assistantService.getLatestForm();
        return R.ok(resp);
    }


}
