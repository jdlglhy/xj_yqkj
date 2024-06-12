package com.ry.yqkj.framework.interceptor;

import com.alibaba.fastjson2.JSON;
import com.ry.yqkj.common.constant.Constants;
import com.ry.yqkj.common.constant.HttpStatus;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.utils.StringUtils;
import com.ry.yqkj.system.domain.dto.CodeSessionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author : lihy
 * @Description : 微信小程序拦截器
 * @date : 2024/5/22 11:30 下午
 */
@Slf4j
@Component
public class WxAppletInterceptor implements HandlerInterceptor {


    @Resource
    private RedisCache redisCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(Constants.MD5_SESSION_KEY);
        if (StringUtils.isEmpty(header)) {
            //拦截没有权限
            R result = R.fail(403, "forbidden");
            returnErrorResponse(response, result);
            return false;
        } else {
            CodeSessionModel codeSession = redisCache.getCacheObject(header);
            if (codeSession == null) {
                //未授权
                R result = R.fail(HttpStatus.UNAUTHORIZED, "unauthorized");
                returnErrorResponse(response, result);
                return false;
            }
        }
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, R result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JSON.toJSONBytes(result));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
