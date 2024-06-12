package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.TableDataInfo;
import com.ry.yqkj.model.req.web.assist.AssistFormExamReq;
import com.ry.yqkj.system.service.IAssistantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.Authorization;
import org.apache.commons.compress.utils.Lists;
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


    /**
     * 助教申请列表
     */
    @PreAuthorize("@ss.hasPermi('assist:form:list')")
    @GetMapping("/form/page")
    public TableDataInfo applyList() {
        return getDataTable(Lists.newArrayList());
    }

    /**
     * 审批
     */
    @PreAuthorize("@ss.hasPermi('assist:form:examine')")
    @PostMapping("/from/examine")
    public R<Void> examine(@RequestBody @Validated AssistFormExamReq req) {
        assistantService.examine(req);
        return R.ok();
    }

    /**
     * 申请详情
     */
    @PreAuthorize("@ss.hasPermi('assist:form:info')")
    @GetMapping("/form/info")
    public R<Void> applyInfo() {
        return R.ok();
    }

}
