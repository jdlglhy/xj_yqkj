package com.ry.yqkj.common.core.domain.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信小程序用户
 *
 * @author ry.yqkj
 */
@Data
@Builder
public class WxAppUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 微信openId
     */
    private String openId;
}
