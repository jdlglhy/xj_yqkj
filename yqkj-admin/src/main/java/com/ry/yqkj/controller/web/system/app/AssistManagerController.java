package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.TableDataInfo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : lihy
 * @Description : 助教管理Controller
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/assist")
public class AssistManagerController extends BaseController {


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
    @GetMapping("/from/examine")
    public R<Void> examine() {
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
