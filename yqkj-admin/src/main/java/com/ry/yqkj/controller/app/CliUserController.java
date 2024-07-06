package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.app.cliuser.CliUserInfoSetReq;
import com.ry.yqkj.model.resp.app.cliuser.CliUserInfoResp;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.service.IAssistantService;
import com.ry.yqkj.system.service.ICliUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.C;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 用户Controller
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("前台用户管理")
public class CliUserController extends WxBaseController {

    @Resource
    private ICliUserService cliUserService;

    @Resource
    private AssistComponent assistComponent;

    @PostMapping("/cli_user/set_simple_info")
    @ApiOperation("设置基本信息")
    public R<Void> setSimpleInfo(@Validated @RequestBody CliUserInfoSetReq req) {
        cliUserService.setUserSimpleInfo(req);
        return R.ok();
    }

    @GetMapping("/cli_user/info")
    @ApiOperation("获取当前用户信息")
    public R<CliUserInfoResp> currentUserInfo() {
        WxAppUser wxAppUser = getCurrent();
        CliUser cliUser = cliUserService.getById(wxAppUser.getUserId());
        CliUserInfoResp cliUserInfoResp = DozerUtil.map(cliUser, CliUserInfoResp.class);
        Assistant assistant = assistComponent.getAssistant(cliUser.getId());
        if(assistant != null){
            cliUserInfoResp.setAssistId(assistant.getId());
        }
        return R.ok(cliUserInfoResp);
    }
}