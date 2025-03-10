package com.ry.yqkj.system.component;

import cn.binarywang.wx.miniapp.api.WxMaLinkService;
import cn.binarywang.wx.miniapp.api.impl.WxMaLinkServiceImpl;
import cn.binarywang.wx.miniapp.bean.urllink.GenerateUrlLinkRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.helper.WxConfigHelper;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author : lihy
 * @Description : todo
 * @date : 2025/2/28 9:25 下午
 */
@Component
@Slf4j
public class WxCommonComponent {

    @Resource
    private IWxUserService wxUserService;
    @Resource
    private WxConfigHelper wxConfigHelper;

    public String getWxPhone(String encryptedData, String iv) {
        WxAppUser wxAppUser = WxUserUtils.current();
        WxUser wxUser = wxUserService.getWxUserByCliUserId(wxAppUser.getUserId());
        try {
            String data = decryptedData(encryptedData, iv, wxUser.getSessionKey());
            log.info("wxUserId={},data={}", wxUser.getId(), data);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);
            String phone = jsonNode.get("phoneNumber").asText();
            if (StringUtils.isNoneBlank(phone)) {
                wxUser.setPhone(phone);
                wxUserService.updateById(wxUser);
            }
            return phone;
        } catch (Exception e) {
            log.error("获取手机号失败:encryptedData={},iv={}", encryptedData, iv, e);
        }
        return "";
    }

    private String decryptedData(String encryptedData, String iv, String sessionKey) throws Exception {

        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        // 创建AES密钥和IV参数
        SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        // 初始化AES解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // 解密
        byte[] decryptedData = cipher.doFinal(encryptedDataBytes);
        // 将字节转换为字符串
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    public String generateUrlLink() {
        try {
            WxMaLinkService wxMaLinkService = new WxMaLinkServiceImpl(WxConfigHelper.getWxMaService());
            GenerateUrlLinkRequest request = new GenerateUrlLinkRequest();
            return wxMaLinkService.generateUrlLink(request);
        } catch (Exception e) {
            log.error("生成小程序urlLink异常", e);
        }
        return "";
    }
}
