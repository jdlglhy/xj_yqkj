package com.ry.yqkj.framework.app;

import com.ry.yqkj.common.constant.Constants;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.ServletUtils;
import com.ry.yqkj.common.utils.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * @author : lihy
 * @Description : 小程序用户信息获取
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
            throw new ServiceException("缺少授权请求头参数！",HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
        }
        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        CodeSessionModel session = redisCache.getCacheObject(md5SessionKey);
        if (session == null) {
            throw new ServiceException("未授权！",HttpStatus.UNAUTHORIZED.value());
        }
        return WxAppUser.builder()
                .userId(session.getUserId())
                .openId(session.getOpenid())
                .build();
    }

}
