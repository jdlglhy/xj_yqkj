package com.ry.yqkj.model.resp;


import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 微信小程序授权DTO
 * @date : 2024/5/21 12:18 上午
 */
@Data
public class CodeSessionResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 微信sessionId
     */
    private String openid;
    /**
     * header 参数
     */
    private String md5SessionKey;
}
