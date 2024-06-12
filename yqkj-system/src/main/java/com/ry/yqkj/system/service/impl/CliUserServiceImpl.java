package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.model.req.app.cliuser.CliUserInfoSetReq;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.mapper.app.CliUserMapper;
import com.ry.yqkj.system.service.ICliUserService;
import lombok.extern.slf4j.Slf4j;
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
public class CliUserServiceImpl extends ServiceImpl<CliUserMapper, CliUser> implements ICliUserService {

    @Resource
    private CliUserMapper cliUserMapper;

    @Override
    public void setUserSimpleInfo(CliUserInfoSetReq cliUserInfoSetReq) {
        WxAppUser wxAppUser = WxUserUtils.current();
        CliUser cliUser = cliUserMapper.selectById(wxAppUser.getUserId());
        if (cliUser == null) {
            throw new ServiceException("找不到对应的用户信息");
        }
        DozerUtil.map(cliUserInfoSetReq, cliUser);
        cliUser.setModifyTime(new Date());
        cliUserMapper.updateById(cliUser);
    }
}
