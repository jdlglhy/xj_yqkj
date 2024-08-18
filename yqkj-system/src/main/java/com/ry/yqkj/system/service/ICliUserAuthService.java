package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.model.resp.app.cliuser.CliUserAuthResp;
import com.ry.yqkj.system.domain.CliUserAuth;

/**
 * @author : lihy
 * @Description : 客户端用户身份标识 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface ICliUserAuthService extends IService<CliUserAuth> {


    /**
     * 是否是区域代理、是的话返回对应的系统账户ID
     *
     * @param cliUserId 客户端用户ID
     * @return 区代理返回对应的系统账户ID
     */
    Long isAreaAgent(Long cliUserId);

    /**
     * 是否是助教、是否是区域代理、是的话返回对应的助教ID
     *
     * @param cliUserId 客户端用户ID
     * @return 助教的话返回助教ID
     */
    Long isAssistant(Long cliUserId);

    /**
     * 是否是入驻球馆、是否是区域代理、是的话返回对应的系统账户ID
     *
     * @param cliUserId 客户端用户ID
     * @return 球馆入驻返回对应的系统账户ID
     */
    Long isBilliardHall(Long cliUserId);

    /**
     * 获取当前用户拥有的身份信息
     *
     * @param cliUserId 客户端用户ID
     * @return 身份信息
     */
    CliUserAuthResp currentUserAuth(Long cliUserId);

}
