package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.system.domain.WxUser;
import com.ry.yqkj.system.domain.dto.CodeSessionDTO;

/**
 * @author : lihy
 * @Description : 微信用户信息 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IWxUserService extends IService<WxUser> {
    /**
     * 绑定微信用户
     *
     * @param code
     * @return
     */
    CodeSessionModel bindWxUser(String code);


}
