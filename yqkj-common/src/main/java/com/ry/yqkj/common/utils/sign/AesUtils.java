package com.ry.yqkj.common.utils.sign;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 解密支付回调工具类
 */
public class AesUtils {
    static final int KEY_LENGTH_BYTE = 32;
    static final int TAG_LENGTH_BIT = 128;
    private final byte[] aesKey;

    public AesUtils(byte[] key) {
        if (key.length != KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
        }
        this.aesKey = key;
    }

    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
            throws GeneralSecurityException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 解密支付回调返回的resource对象
     * 包含支付的所有信息
     *
     * @param apiV3Key   小程序微信支付密钥key
     * @param jsonObject
     * @return
     */
    public static JSONObject decryptResource(String apiV3Key, JSONObject jsonObject) {
        String json = jsonObject.toString();
        //解密resource对象
        String associated_data = (String) JSONUtil.getByPath(JSONUtil.parse(json), "resource.associated_data");
        String ciphertext = (String) JSONUtil.getByPath(JSONUtil.parse(json), "resource.ciphertext");
        String nonce = (String) JSONUtil.getByPath(JSONUtil.parse(json), "resource.nonce");
        String decryptData;
        try {
            decryptData = new AesUtils(apiV3Key.getBytes(StandardCharsets.UTF_8))
                    .decryptToString(associated_data.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return JSONObject.parseObject(decryptData, JSONObject.class);
    }

    //decryptDataObj 为解码后的obj，其内容如下。之后便是验签成功后的业务处理
    //{
    //	"sp_appid": "wx8888888888888888",
    //	"sp_mchid": "1230000109",
    //	"sub_appid": "wxd678efh567hg6999",
    //	"sub_mchid": "1900000109",
    //	"out_trade_no": "1217752501201407033233368018",
    //	"trade_state_desc": "支付成功",
    //	"trade_type": "MICROPAY",
    //	"attach": "自定义数据",
    //	"transaction_id": "1217752501201407033233368018",
    //	"trade_state": "SUCCESS",
    //	"bank_type": "CMC",
    //	"success_time": "2018-06-08T10:34:56+08:00",
    //    ...
    //	"payer": {
    //		"openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"
    //	},
    //	"scene_info": {
    //		"device_id": "013467007045764"
    //	}
    //}
}
