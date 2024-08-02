package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.web.agent.AgentExamineRequest;
import com.ry.yqkj.model.req.web.agent.AgentUpdateRequest;
import com.ry.yqkj.model.req.web.agent.AreaAgentPageReq;
import com.ry.yqkj.model.resp.web.agent.AreaAgentResp;
import com.ry.yqkj.system.service.IAreaAgentService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 后台-助教管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/area_agent")
@Api("后台-区域代理")
public class AreaAgentManagerController extends BaseController {


    @Resource
    private IAreaAgentService areaAgentService;

    /**
     * 区域代理列表
     */
    @PreAuthorize("@ss.hasPermi('area_agent:list')")
    @PostMapping("/page")
    public R<PageResDomain<AreaAgentResp>> page(@Validated @RequestBody AreaAgentPageReq areaAgentPageReq) {
        return R.ok(areaAgentService.page(areaAgentPageReq));
    }

    /**
     * 审批
     */
    @PreAuthorize("@ss.hasPermi('area_agent:examine')")
    @PostMapping("/examine")
    public R<Void> examine(@RequestBody @Validated AgentExamineRequest req) {
        areaAgentService.examine(req.getId(), req.getStatus(), req.getRefuseReason());
        return R.ok();
    }

    /**
     * 更新代理信息
     */
    @PreAuthorize("@ss.hasPermi('area_agent:update')")
    @PostMapping("/update")
    public R<Void> updateAgent(@Validated @RequestBody AgentUpdateRequest request) {
        areaAgentService.updateAgent(request);
        return R.ok();
    }

}
