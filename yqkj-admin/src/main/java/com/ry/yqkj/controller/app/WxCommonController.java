package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.model.req.app.EncryptedDataRequest;
import com.ry.yqkj.system.component.WxCommonComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 微信公用接口管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@Api("微信公用接口管理")
public class WxCommonController extends WxBaseController {

    @Resource
    private WxCommonComponent wxCommonComponent;


    @PostMapping("/common/decrypt_phone_data")
    @ApiOperation("获取用户授权的手机号")
    public R<String> decryptPhoneData(@Validated @RequestBody EncryptedDataRequest encryptedData) {
        return R.ok(wxCommonComponent.getWxPhone(encryptedData.getEncryptedData(), encryptedData.getIv()));
    }
}
