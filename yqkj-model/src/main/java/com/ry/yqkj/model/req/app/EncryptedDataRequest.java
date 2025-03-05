package com.ry.yqkj.model.req.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 加密数据参数
 * @date : 2025/3/1 10:09 下午
 */
@Data
public class EncryptedDataRequest implements Serializable {
    /**
     * 授权加密数据
     */
    @JsonProperty("encryptedData")
    @NotBlank(message = "encryptedData参数缺失")
    private String encryptedData;


    /**
     * 矢量
     */
    @JsonProperty("iv")
    @NotBlank(message = "iv参数缺失")
    private String iv;

}
