package com.ry.yqkj.system.component;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.service.IAssistantService;
import com.ry.yqkj.system.service.ICliUserService;
import com.ry.yqkj.system.service.IWxUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AssistComponent {

    @Resource
    private IAssistantService assistantService;
    @Resource
    private IWxUserService wxUserService;
    @Resource
    private ICliUserService cliUserService;

    /**
     * 通过用户获取助教
     *
     * @return 助教
     */
    public Assistant getAssistant(Long userId) {
        LambdaQueryWrapper<Assistant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assistant::getCliUserId, userId);
        List<Assistant> assistantList = assistantService.list(wrapper);
        return CollUtil.isNotEmpty(assistantList) ? assistantList.get(0) : null;
    }

    /**
     * 通过助教id获取wxUser中的手机号
     *
     * @return 手机号
     */
    public String getPhoneByAssistId(Long assistId) {

        Assistant assistant = assistantService.getById(assistId);
        if(StringUtils.isNoneBlank(assistant.getPhone())){
            return assistant.getPhone();
        }
        return getPhoneByUserId(assistant.getCliUserId());
    }


    /**
     * 通过用户id获取手机号
     *
     * @return 手机号
     */
    public String getPhoneByUserId(Long clientUserId) {

        CliUser cliUser = cliUserService.getById(clientUserId);
        if(StringUtils.isNoneBlank(cliUser.getPhone())){
            return cliUser.getPhone();
        }
        return wxUserService.getWxUserByCliUserId(cliUser.getWxUserId()).getPhone();
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
