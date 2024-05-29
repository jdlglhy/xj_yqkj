package com.ry.yqkj.common.utils.sign;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author : lihy
 * @Description : des 加解密
 * @date : 2024/5/21 12:05 上午
 */
@Slf4j
public class DESUtils {

    private static final String ALGORITHM = "DES";
    /**
     * 密钥（默认只允许8个字节）
     */
    private static final String SECRET = "wx@yqkj!";

    public static String encrypt(String data) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){
            log.error("DESUtils#encrypt failed!",e);
        }
        return "";
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(java.util.Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    }
}
