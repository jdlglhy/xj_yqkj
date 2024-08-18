package com.ry.yqkj.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.entity.SysUser;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.StringUtils;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.model.common.vo.AreaVO;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.enums.UserTypeEnum;
import com.ry.yqkj.model.req.app.agent.AreaAgentApplyRequest;
import com.ry.yqkj.model.req.web.agent.WebAgentUpdateRequest;
import com.ry.yqkj.model.req.web.agent.WebAreaAgentPageReq;
import com.ry.yqkj.model.resp.web.agent.WebAreaAgentResp;
import com.ry.yqkj.system.domain.AreaAgent;
import com.ry.yqkj.system.domain.CliUserAuth;
import com.ry.yqkj.system.domain.UserArea;
import com.ry.yqkj.system.mapper.AreaAgentMapper;
import com.ry.yqkj.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author : lihy
 * @Description : 区域代理 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class IAreaAgentServiceImpl extends ServiceImpl<AreaAgentMapper, AreaAgent> implements IAreaAgentService {


    @Resource
    private IAreaService areaService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private IUserAreaService userAreaService;
    @Resource
    private ICliUserAuthService cliUserAuthService;

    @Override
    public void apply(AreaAgentApplyRequest request) {
        Long cliUserId = WxUserUtils.current().getUserId();
        checkCanApply(cliUserId);
        AreaVO areaVO = areaService.getAreaByCode(request.getAreaCode());
        if (areaVO == null) {
            throw new ServiceException("未获取到对应的区域！");
        }
        AreaAgent areaAgent = DozerUtil.map(request, AreaAgent.class);
        areaAgent.setAreaCode(areaVO.getCode());
        areaAgent.setAreaName(areaAgent.getAreaName());
        areaAgent.setCliUserId(cliUserId);
        areaAgent.setLevel(areaAgent.getLevel());
        areaAgent.setStatus(ApproveEnum.APPROVING.code);
        save(areaAgent);
    }

    private void checkCanApply(Long cliUserId) {
        LambdaQueryWrapper<AreaAgent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AreaAgent::getCliUserId, cliUserId);
        wrapper.orderByDesc(AreaAgent::getId);
        wrapper.last("limit 1");
        AreaAgent agent = baseMapper.selectOne(wrapper);
        //验证是否可以申请
        if (agent != null) {
            if (ApproveEnum.APPROVING.code.equals(agent.getStatus())) {
                throw new ServiceException("您已经提交过申请，审批中！");
            }
            if (ApproveEnum.APPROVED.code.equals(agent.getStatus())) {
                throw new ServiceException("您已经是代理，请勿重复申请！");
            }
        }
    }

    private AreaAgent checkCanExamine(Long id, String status, String refuseReason) {
        AreaAgent areaAgent = baseMapper.selectById(id);
        if (areaAgent == null) {
            throw new ServiceException("未获取到对应的数据！");
        }
        if (!(ObjectUtil.equal(status, ApproveEnum.APPROVED.code) || ObjectUtil.equal(status, ApproveEnum.REFUSED.code))) {
            throw new ServiceException("未获取到对应的数据！");
        }
        if (ObjectUtil.notEqual(areaAgent.getStatus(), ApproveEnum.APPROVING.code)) {
            throw new ServiceException("状态异常 ！");
        }
        if (ApproveEnum.REFUSED.code.equals(status)) {
            if (StringUtils.isBlank(refuseReason)) {
                throw new ServiceException("请填写未通过的原因 ！");
            }
        }
        return areaAgent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(Long id, String status, String reason) {
        AreaAgent areaAgent = checkCanExamine(id, status, reason);
        areaAgent.setStatus(status);
        if (ApproveEnum.REFUSED.code.equals(status) && StringUtils.isBlank(reason)) {
            throw new ServiceException("请填写不通过的原因！");
        }
        areaAgent.setRefuseReason(reason);
        baseMapper.updateById(areaAgent);
        if (ApproveEnum.APPROVED.code.equals(status)) {
            Long targetId = cliUserAuthService.isAreaAgent(areaAgent.getCliUserId());
            SysUser sysUser = null;
            if (targetId != null) {
                sysUser = sysUserService.selectUserById(targetId);
            } else {
                //创建系统账号、绑定区域
                sysUser = new SysUser();
                sysUser.setUserName(areaAgent.getRealName());
                sysUser.setNickName(areaAgent.getRealName());
                sysUser.setPhonenumber(areaAgent.getContact());
                sysUser.setUserType(UserTypeEnum.AREA_AGENT.getCode());
                sysUser.setEmail(areaAgent.getEmail());
                if (!sysUserService.checkUserNameUnique(sysUser)) {
                    throw new ServiceException("生成系统账户异常，用户名【" + areaAgent.getRealName() + "】已存在！");
                }
                sysUserService.insertUser(sysUser);
                CliUserAuth cliUserAuth = new CliUserAuth();
                cliUserAuth.setTargetId(sysUser.getUserId());
                cliUserAuth.setUserType(UserTypeEnum.AREA_AGENT.getCode());
                cliUserAuth.setCliUserId(areaAgent.getCliUserId());
                cliUserAuthService.save(cliUserAuth);
            }

            UserArea userArea = new UserArea();
            userArea.setAreaCode(areaAgent.getAreaCode());
            userArea.setAreaName(areaAgent.getAreaName());
            userArea.setLevel(areaAgent.getLevel());
            userArea.setUserId(sysUser.getUserId());
            userAreaService.save(userArea);

            areaAgent.setUserId(sysUser.getUserId());
            this.updateById(areaAgent);
        }
    }

    @Override
    public void updateAgent(WebAgentUpdateRequest request) {
        AreaAgent areaAgent = this.getById(request.getId());
        //审批中可以进行信息变更
        if (ObjectUtil.notEqual(ApproveEnum.APPROVED.code, areaAgent.getStatus())) {
            throw new ServiceException("审批中才能进行变更！");
        }
        if (StringUtils.isNoneBlank(request.getContact())) {
            areaAgent.setContact(request.getContact());
        }
        if (StringUtils.isNoneBlank(request.getEmail())) {
            areaAgent.setEmail(request.getEmail());
        }
        this.updateById(areaAgent);
    }

    @Override
    public PageResDomain<WebAreaAgentResp> page(WebAreaAgentPageReq req) {
        QueryWrapper<AreaAgent> wrapper = SearchTool.invoke(req);
        wrapper.lambda().eq(AreaAgent::getStatus, ApproveEnum.APPROVED.code);
        Page<AreaAgent> page = new Page<>(req.getCurrent(), req.getPageSize());
        page = baseMapper.selectPage(page, wrapper);

        if (page.getTotal() <= 0) {
            return new PageResDomain<>();
        }
        return PageResDomain.parse(page, WebAreaAgentResp.class);
    }

    @Override
    public PageResDomain<WebAreaAgentResp> examinePage(WebAreaAgentPageReq req) {
        QueryWrapper<AreaAgent> wrapper = SearchTool.invoke(req);
        Page<AreaAgent> page = new Page<>(req.getCurrent(), req.getPageSize());
        page = baseMapper.selectPage(page, wrapper);

        if (page.getTotal() <= 0) {
            return new PageResDomain<>();
        }
        return PageResDomain.parse(page, WebAreaAgentResp.class);
    }

    @Override
    public WebAreaAgentResp detail() {
        Long wxUserId = WxUserUtils.current().getUserId();
        LambdaQueryWrapper<AreaAgent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AreaAgent::getCliUserId, wxUserId);
        wrapper.orderByDesc(AreaAgent::getId);
        wrapper.last("limit 1");
        AreaAgent agent = baseMapper.selectOne(wrapper);
        if (agent != null) {
            return DozerUtil.map(agent, WebAreaAgentResp.class);
        }
        return null;
    }

}
