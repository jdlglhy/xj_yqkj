package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.model.req.app.assist.AssistApplyReq;
import com.ry.yqkj.model.req.app.assist.AssistPageReq;
import com.ry.yqkj.model.req.app.assist.AssistRecPageReq;
import com.ry.yqkj.model.resp.app.assist.AssistDetailResp;
import com.ry.yqkj.model.resp.app.assist.AssistFormInfoResp;
import com.ry.yqkj.model.resp.app.assist.AssistInfoResp;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.service.IAssistantService;
import com.ry.yqkj.system.service.impl.AssistantServiceImpl;
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
@Api("前台助教管理")
public class AssistController extends WxBaseController {

    @Resource
    private IAssistantService assistantService;
    @Resource
    private AssistComponent assistComponent;


    @PostMapping("/assist/page")
    @ApiOperation("分页列表")
    public R<PageResDomain<AssistInfoResp>> page(@Validated @RequestBody AssistPageReq req) {
        return R.ok(assistantService.assistPage(req));
    }

    @GetMapping("/assist/{assistId}")
    @ApiOperation("助教详情")
    public R<AssistDetailResp> assistDetail(@PathVariable("assistId") Long assistId) {
        return R.ok(assistantService.assistDetail(assistId));
    }

    @PostMapping("/assist/form")
    @ApiOperation("申请")
    public R<Void> apply(@Validated @RequestBody AssistApplyReq req) {
        assistantService.apply(req);
        return R.ok();
    }

    @GetMapping("/assist/form/get")
    @ApiOperation("获取当前用户的申请信息")
    public R<AssistFormInfoResp> assistFormInfo() {
        AssistFormInfoResp resp = assistantService.getLatestForm(WxUserUtils.current().getUserId());
        return R.ok(resp);
    }

    @GetMapping("/assist/recommend/page")
    @ApiOperation("助教推荐")
    public R<PageResDomain<AssistInfoResp>> assistRecommend(@Validated @RequestBody AssistRecPageReq assistRecPageReq) {
        return R.ok(assistantService.assistRecPage(assistRecPageReq));
    }


}
