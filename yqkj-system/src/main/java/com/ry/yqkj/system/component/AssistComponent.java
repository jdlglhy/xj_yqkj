package com.ry.yqkj.system.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.service.impl.AssistantServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AssistComponent {

    @Resource
    private AssistantServiceImpl assistantService;

    /**
     * 通过用户获取助教
     *
     * @return 助教
     */
    public Assistant getAssistant(Long userId) {
        LambdaQueryWrapper<Assistant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assistant::getCliUserId, userId);
        return assistantService.getOne(wrapper);
    }

    /**
     * 判断用户是否有助教身份
     *
     * @return 助教
     */
    public boolean isAssist(Long userId) {
        return getAssistant(userId) != null;
    }

    /**
     * 获取当前用户的助教身份信息
     *
     * @return 助教信息
     */
    public Assistant currentUserToAssistant() {
        WxAppUser user = WxUserUtils.current();
        return getAssistant(user.getUserId());
    }

}
