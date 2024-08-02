package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.cliuser.EvalPageRequest;
import com.ry.yqkj.model.resp.app.cliuser.OrderEvalResp;
import com.ry.yqkj.system.service.IOrderEvalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 评价管理
 * @date : 2024/7/8 12:11 上午
 */
@RestController
@Api("评价管理")
public class EvalController extends WxBaseController {

    @Resource
    private IOrderEvalService orderEvalService;

    @PostMapping("/eval/assist/eval_page")
    @ApiOperation("助教详情【评价列表】")
    public R<PageResDomain<OrderEvalResp>> assistEvalPage(@RequestBody @Validated EvalPageRequest evalPageRequest) {
        return R.ok(orderEvalService.assistEvalPage(evalPageRequest));
    }
}
