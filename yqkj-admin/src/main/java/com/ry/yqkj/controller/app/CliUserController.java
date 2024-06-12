package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.model.req.app.cliuser.CliUserInfoSetReq;
import com.ry.yqkj.system.service.ICliUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
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
@Api("用户信息管理")
public class CliUserController extends WxBaseController {

    @Resource
    private ICliUserService cliUserService;

    @PostMapping("/cli_user/set_simple_info")
    @ApiOperation("设置基本信息")
    public R<Void> setSimpleInfo(@Validated @RequestBody CliUserInfoSetReq req) {
        cliUserService.setUserSimpleInfo(req);
        return R.ok();
    }


}
