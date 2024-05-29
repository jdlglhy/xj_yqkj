package com.ry.yqkj.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 微信小程序授权DTO
 * @date : 2024/5/21 12:18 上午
 */
@Data
public class CodeSessionDTO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 微信sessionId
     */
    private String openid;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 微信session_key
     */
    private String session_key;

    /**
     * header 参数
     */
    private String md5SessionKey;
}
