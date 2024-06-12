package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.app.WxAuthorizeReq;
import com.ry.yqkj.model.resp.CodeSessionResp;
import com.ry.yqkj.system.service.IWxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 微信授权管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/wx_api")
@Api("微信API")
public class WxApiController extends BaseController {

    @Resource
    private IWxUserService wxUserService;

    @PostMapping("/auth")
    @ApiOperation("微信授权方法（通过code登陆）")
    public R<CodeSessionResp> bind(@Validated @RequestBody WxAuthorizeReq req) {
        CodeSessionModel codeSession = wxUserService.bindWxUser(req.getCode());
        return R.ok(DozerUtil.map(codeSession, CodeSessionResp.class));
    }


}
