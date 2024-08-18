package com.ry.yqkj.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.constant.CacheConstants;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.http.HttpUtils;
import com.ry.yqkj.common.utils.sign.Md5Utils;
import com.ry.yqkj.common.utils.uuid.SnowflakeIdUtil;
import com.ry.yqkj.system.component.AssistComponent;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.mapper.app.WxUserMapper;
import com.ry.yqkj.system.service.ICliUserService;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : lihy
 * @Description : 微信用户信息 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements IWxUserService {

    @Value("${wx.app.appId}")
    private String appId;
    @Value("${wx.app.secret}")
    private String secret;

    @Resource
    private RedisCache redisCache;
    @Resource
    private ICliUserService cliUserService;
    @Resource
    private IWxUserService wxUserService;

    @Resource
    private AssistComponent assistComponent;

    @Value("${wx.test.auCodes}")
    private String wxTestAuCodes;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeSessionModel bindWxUser(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        /**
         * 拼接需要的参数
         * appid = AppID 你自己的微信小程序APPID
         * js_code = AppSecret 你自己的微信APP密钥
         * grant_type=authorization_code = code 微信官方提供的临时凭证
         */
        String params = MessageFormat.format("appid={0}&secret={1}&js_code={2}&grant_type=authorization_code", appId,
                secret, code);

        String md5SessionKey = "";

        CodeSessionModel codeSession = new CodeSessionModel();
        List<String> codes = Arrays.asList(StringUtils.split(wxTestAuCodes, ","));
        if (codes.contains(code)) {
            codeSession.setSession_key(code);
            codeSession.setOpenid(code);
        } else {
            //开始发起网络请求,若依管理系统自带网络请求工具，直接使用即可
            String res = HttpUtils.sendGet(url, params);
            log.info("code={},res ={}", code, res);
            codeSession = JSON.parseObject(res, CodeSessionModel.class);
            if (codeSession == null || StringUtils.isEmpty(codeSession.getOpenid())) {
                throw new ServiceException("未获取到对应的openid");
            }
        }
        md5SessionKey = Md5Utils.hash(codeSession.getSession_key());
        codeSession.setMd5SessionKey(md5SessionKey);
        log.info("md5SessionKey={},openId={},session_key={}", md5SessionKey, codeSession.getOpenid(),
                codeSession.getSession_key());
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, codeSession.getOpenid());
        WxUser wxUser = this.getOne(wrapper);
        CliUser cliUser = null;
        if (wxUser == null) {
            Long cliUserId = SnowflakeIdUtil.nextId();
            wxUser = new WxUser();
            wxUser.setOpenId(codeSession.getOpenid());
            wxUser.setSessionKey(codeSession.getSession_key());
            wxUser.setCliUserId(cliUserId);
            wxUser.setCreateTime(new Date());
            log.info("save wxUser result={}", wxUserService.save(wxUser));
            //同时绑定创建一个用户信息
            cliUser = new CliUser();
            cliUser.setId(cliUserId);
            cliUser.setWxUserId(wxUser.getId());
            cliUser.setNickName("游客_" + wxUser.getId());
            cliUser.setCreateTime(new Date());
            cliUser.setMark("client");
            wxUser.setCliUserId(cliUser.getId());
            log.info("save cliUser result={}", cliUserService.save(cliUser));
        } else {
            //清空原来的md5SessionKey
            String oldMd5SessionKey = Md5Utils.hash(wxUser.getSessionKey());
            if (redisCache.hasKey(CacheConstants.SESSION_KEY_PRE + oldMd5SessionKey)) {
                redisCache.deleteObject(CacheConstants.SESSION_KEY_PRE + oldMd5SessionKey);
            }
            wxUser.setSessionKey(codeSession.getSession_key());
            wxUser.setModifyTime(new Date());
            wxUserService.updateById(wxUser);
            LambdaQueryWrapper<CliUser> cliWrap = new LambdaQueryWrapper<>();
            cliWrap.eq(CliUser::getWxUserId, wxUser.getId());
            cliUser = cliUserService.getOne(cliWrap, true);

            //判断是否是助教身份
            Assistant assistant = assistComponent.getAssistant(cliUser.getId());
            if (assistant != null) {
                codeSession.setAssistId(assistant.getId());
            }
        }
        codeSession.setUserId(cliUser.getId());
        redisCache.setCacheObject(CacheConstants.SESSION_KEY_PRE + codeSession.getMd5SessionKey(), codeSession, CacheConstants.SESSION_KEY_EXPIRE_DAYS, TimeUnit.DAYS);

        //清空sessionKey
        codeSession.setSession_key("");
        return codeSession;
    }

    @Override
    public WxUser getWxUserByCliUserId(Long cliUserId) {
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getCliUserId, cliUserId);
        return baseMapper.selectOne(wrapper);
    }


}
