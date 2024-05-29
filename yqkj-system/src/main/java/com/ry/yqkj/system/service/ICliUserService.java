package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.model.req.cliuser.CliUserInfoSetReq;
import com.ry.yqkj.system.domain.CliUser;

/**
 * @author : lihy
 * @Description : 用户信息 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface ICliUserService extends IService<CliUser> {

    /**
     * 设置 cliUser 基础信息
     *
     * @param cliUserInfoSetReq
     */
    void setUserSimpleInfo(CliUserInfoSetReq cliUserInfoSetReq);
}
