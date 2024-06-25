package com.ry.yqkj.common.utils;

import com.ry.yqkj.common.constant.CacheConstants;
import com.ry.yqkj.common.constant.Constants;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.utils.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : lihy
 * @Description : todo
 * @date : 2024/5/23 12:55 上午
 */
public class WxUserUtils {
    /**
     * 获取微信用户信息
     *
     * @return
     */
    public static WxAppUser current() {
        String md5SessionKey = ServletUtils.getRequest().getHeader(Constants.MD5_SESSION_KEY);
        if (StringUtils.isBlank(md5SessionKey)) {
            return WxAppUser.builder().build();
        }
        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        CodeSessionModel session = redisCache.getCacheObject(CacheConstants.SESSION_KEY_PRE + md5SessionKey);
        if (session == null) {
            return WxAppUser.builder().build();
        }
        return WxAppUser.builder()
                .userId(session.getUserId())
                .openId(session.getOpenid())
                .assistId(session.getAssistId())
                .build();
    }
}
