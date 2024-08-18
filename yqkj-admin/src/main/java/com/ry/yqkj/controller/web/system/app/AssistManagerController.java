package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.web.assist.WebAssistFormExamReq;
import com.ry.yqkj.model.req.web.assist.WebAssistFormPageReq;
import com.ry.yqkj.model.req.web.assist.WebAssistPageReq;
import com.ry.yqkj.model.resp.app.assist.AssistFormInfoResp;
import com.ry.yqkj.model.resp.web.assist.WebAssistInfoResp;
import com.ry.yqkj.system.domain.AssistForm;
import com.ry.yqkj.system.mapper.app.AssistFormMapper;
import com.ry.yqkj.system.service.IAssistantService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 后台-助教管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/assist")
@Api("后台-助教管理")
public class AssistManagerController extends BaseController {

    @Resource
    private IAssistantService assistantService;
    @Resource
    private AssistFormMapper assistFormMapper;


    /**
     * 助教申请列表
     */
    @PreAuthorize("@ss.hasPermi('assist:form:list')")
    @PostMapping("/form/page")
    public R<PageResDomain<AssistFormInfoResp>> applyList(@Validated @RequestBody WebAssistFormPageReq webAssistFormPageReq) {
        return R.ok(assistantService.fromPage(webAssistFormPageReq));
    }

    /**
     * 助教列表
     */
    @PreAuthorize("@ss.hasPermi('assist:list')")
    @PostMapping("/page")
    public R<PageResDomain<WebAssistInfoResp>> page(@Validated @RequestBody WebAssistPageReq assistPageReq) {
        return R.ok(assistantService.page(assistPageReq));
    }

    /**
     * 审批
     */
    @PreAuthorize("@ss.hasPermi('assist:form:examine')")
    @PostMapping("/from/examine")
    public R<Void> examine(@RequestBody @Validated WebAssistFormExamReq req) {
        assistantService.examine(req);
        return R.ok();
    }

    /**
     * 申请详情
     */
    @PreAuthorize("@ss.hasPermi('assist:form:info')")
    @GetMapping("/form/info/{formId}")
    public R<AssistFormInfoResp> applyInfo(@PathVariable("formId") Long formId) {
        AssistForm assistForm = assistFormMapper.selectById(formId);
        if (assistForm == null) {
            throw new ServiceException("未找到对应表单！");
        }
        return R.ok(DozerUtil.map(assistForm, AssistFormInfoResp.class));
    }

}
