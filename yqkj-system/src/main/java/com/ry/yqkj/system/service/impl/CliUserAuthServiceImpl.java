package com.ry.yqkj.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.utils.Assert;
import com.ry.yqkj.model.enums.UserTypeEnum;
import com.ry.yqkj.model.resp.app.cliuser.CliUserAuthResp;
import com.ry.yqkj.system.domain.CliUserAuth;
import com.ry.yqkj.system.mapper.CliUserAuthMapper;
import com.ry.yqkj.system.service.ICliUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : lihy
 * @Description : 客户端用户身份标识 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class CliUserAuthServiceImpl extends ServiceImpl<CliUserAuthMapper, CliUserAuth> implements ICliUserAuthService {


    @Override
    public Long isAreaAgent(Long cliUserId) {
        return getTargetUserId(cliUserId, UserTypeEnum.AREA_AGENT.getCode());
    }

    @Override
    public Long isAssistant(Long cliUserId) {
        return getTargetUserId(cliUserId, UserTypeEnum.ASSISTANT.getCode());
    }

    @Override
    public Long isBilliardHall(Long cliUserId) {
        return getTargetUserId(cliUserId, UserTypeEnum.BILLIARD_HALL.getCode());
    }

    @Override
    public CliUserAuthResp currentUserAuth(Long cliUserId) {
        CliUserAuthResp cliUserAuthResp = new CliUserAuthResp();
        LambdaQueryWrapper<CliUserAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CliUserAuth::getCliUserId, cliUserId);
        wrapper.groupBy(CliUserAuth::getUserType);
        List<CliUserAuth> userAuthList = this.baseMapper.selectList(wrapper);
        if (CollUtil.isEmpty(userAuthList)) {
            return cliUserAuthResp;
        }
        userAuthList.forEach(auth -> {
            Assert.doIfTrue(UserTypeEnum.BILLIARD_HALL.code.equals(auth.getUserType()), () -> cliUserAuthResp.setBilliardHall(true));
            Assert.doIfTrue(UserTypeEnum.AREA_AGENT.code.equals(auth.getUserType()), () -> cliUserAuthResp.setAreaAgent(true));
            Assert.doIfTrue(UserTypeEnum.ASSISTANT.code.equals(auth.getUserType()), () -> cliUserAuthResp.setAssist(true));
        });
        return null;
    }

    private Long getTargetUserId(Long cliUserId, String userType) {
        LambdaQueryWrapper<CliUserAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CliUserAuth::getCliUserId, cliUserId);
        wrapper.eq(CliUserAuth::getUserType, userType);
        CliUserAuth cliUserAuth = this.baseMapper.selectOne(wrapper);
        return cliUserAuth != null ? cliUserAuth.getCliUserId() : null;
    }

}
