package com.ry.yqkj.common.core.controller;

import com.ry.yqkj.common.constant.CacheConstants;
import com.ry.yqkj.common.constant.Constants;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 微信通用路径管理
 *
 * @author ry.yqkj
 */
@RestController
@RequestMapping("/wx_app")
public class WxBaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RedisCache redisCache;

    /**
     * 获取登录用户名
     */
    public WxAppUser getCurrent() {
        String md5SessionKey = ServletUtils.getRequest().getHeader(Constants.MD5_SESSION_KEY);
        if (StringUtils.isBlank(md5SessionKey)) {
            return WxAppUser.builder().build();
        }
        CodeSessionModel session = redisCache.getCacheObject(CacheConstants.SESSION_KEY_PRE + md5SessionKey);
        if (session == null) {
            return WxAppUser.builder().build();
        }
        return WxAppUser.builder()
                .userId(session.getUserId())
                .openId(session.getOpenid())
                .build();
    }
}
