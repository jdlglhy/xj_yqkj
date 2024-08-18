package com.ry.yqkj.controller.web.system.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.web.billiardhall.WebHallExamineRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallFillRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallPageReq;
import com.ry.yqkj.model.resp.web.billiardhall.WebBilliardHallInfoResp;
import com.ry.yqkj.system.service.IBilliardHallService;
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
 * @Description : 后台-球馆管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/billiard_hall")
@Api("后台-球馆管理")
public class BilliardHallManagerController extends BaseController {


    @Resource
    private IBilliardHallService billiardHallService;

    /**
     * 球馆审核列表
     */
    @PreAuthorize("@ss.hasPermi('billiard_hall:examine:list')")
    @PostMapping("/examine/page")
    public R<PageResDomain<WebBilliardHallInfoResp>> examinePage(@Validated @RequestBody WebHallPageReq req) {
        return R.ok(billiardHallService.examinePage(req));
    }

    /**
     * 球馆列表
     */
    @PreAuthorize("@ss.hasPermi('billiard_hall:list')")
    @PostMapping("/page")
    public R<PageResDomain<WebBilliardHallInfoResp>> page(@Validated @RequestBody WebHallPageReq req) {
        return R.ok(billiardHallService.page(req));
    }

    /**
     * 审批
     */
    @PreAuthorize("@ss.hasPermi('billiard_hall:examine')")
    @PostMapping("/examine")
    public R<Void> examine(@RequestBody @Validated WebHallExamineRequest req) {
        billiardHallService.examine(req.getId(), req.getStatus(), req.getRefuseReason());
        return R.ok();
    }

    /**
     * 完善球馆信息
     */
    @PreAuthorize("@ss.hasPermi('billiard_hall:fill')")
    @PostMapping("/fill")
    public R<Void> fillHall(@Validated @RequestBody WebHallFillRequest request) {
        billiardHallService.fillBilliardHallInfo(request);
        return R.ok();
    }

}
