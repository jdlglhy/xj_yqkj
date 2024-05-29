package com.ry.yqkj.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.core.redis.RedisCache;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.http.HttpUtils;
import com.ry.yqkj.common.utils.sign.Md5Utils;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.domain.dto.CodeSessionDTO;
import com.ry.yqkj.system.mapper.app.WxUserMapper;
import com.ry.yqkj.system.service.ICliUserService;
import com.ry.yqkj.system.service.IWxUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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

    @Override
    public CodeSessionModel bindWxUser(String code) {
        String url  = "https://api.weixin.qq.com/sns/jscode2session";
        /**
         * 拼接需要的参数
         * appid = AppID 你自己的微信小程序APPID
         * js_code = AppSecret 你自己的微信APP密钥
         * grant_type=authorization_code = code 微信官方提供的临时凭证
         */
        String params = String.format("appid={}&secret={}&js_code={}&grant_type=authorization_code", appId,
                secret, code);
        //开始发起网络请求,若依管理系统自带网络请求工具，直接使用即可
        String res = HttpUtils.sendGet(url,params);
        log.info("code={},res ={}",code,res);
        CodeSessionModel codeSession = JSON.parseObject(res, CodeSessionModel.class);
        if (codeSession == null) {
            throw new ServiceException("未获取到对应的openid");
        }
        String md5SessionKey = Md5Utils.hash(codeSession.getSession_key());
        codeSession.setMd5SessionKey(md5SessionKey);
        log.info("md5SessionKey={},openId={},session_key={}",md5SessionKey,codeSession.getOpenid(),
                codeSession.getSession_key());
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId,codeSession.getOpenid());
        WxUser wxUser = this.getOne(wrapper);
        CliUser cliUser = null;
        if(wxUser == null){
            wxUser = new WxUser();
            wxUser.setOpenId(codeSession.getOpenid());
            wxUser.setSessionKey(codeSession.getSession_key());
            wxUser.setCreateTime(new Date());
            log.info("save wxUser result={}",wxUserService.save(wxUser));
            //同时绑定创建一个用户信息
            cliUser = new CliUser();
            cliUser.setWxUserId(wxUser.getId());
            cliUser.setCreateTime(new Date());
            cliUser.setMark("client");
            cliUserService.save(cliUser);
            log.info("save cliUser result={}",cliUserService.save(cliUser));
        }else{
            //清空原来的md5SessionKey
            redisCache.deleteObject(Md5Utils.hash(wxUser.getSessionKey()));
            wxUser.setSessionKey(codeSession.getSession_key());
            wxUser.setModifyTime(new Date());
            wxUserService.updateById(wxUser);
            LambdaQueryWrapper<CliUser> cliWrap = new LambdaQueryWrapper<>();
            cliWrap.eq(CliUser::getWxUserId,wxUser.getId());
            cliUser = cliUserService.getOne(cliWrap,true);

        }
        codeSession.setUserId(cliUser.getId());
        redisCache.setCacheObject(md5SessionKey,codeSession);
        return codeSession;
    }





}
