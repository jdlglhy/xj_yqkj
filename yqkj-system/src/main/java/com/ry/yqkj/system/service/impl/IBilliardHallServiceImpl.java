package com.ry.yqkj.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.entity.SysUser;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.Assert;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.enums.UserTypeEnum;
import com.ry.yqkj.model.req.app.billiardhall.HallApplyRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallFillRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallPageReq;
import com.ry.yqkj.model.resp.web.billiardhall.WebBilliardHallInfoResp;
import com.ry.yqkj.system.domain.BilliardHall;
import com.ry.yqkj.system.domain.CliUserAuth;
import com.ry.yqkj.system.mapper.BilliardHallMapper;
import com.ry.yqkj.system.service.IBilliardHallService;
import com.ry.yqkj.system.service.ICliUserAuthService;
import com.ry.yqkj.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;


/**
 * @author : lihy
 * @Description : 球馆 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class IBilliardHallServiceImpl extends ServiceImpl<BilliardHallMapper, BilliardHall> implements IBilliardHallService {


    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ICliUserAuthService cliUserAuthService;

    @Override
    public void apply(HallApplyRequest request) {
        Long cliUserId = WxUserUtils.current().getUserId();
        checkCanApply(cliUserId);
        BilliardHall billiardHall = DozerUtil.map(request, BilliardHall.class);
        billiardHall.setCliUserId(cliUserId);
        billiardHall.setStatus(ApproveEnum.APPROVING.code);
        billiardHall.setCreateBy(UserTypeEnum.CLI_USER.getCode() + "_" + cliUserId);
        save(billiardHall);
    }

    private void checkCanApply(Long cliUserId) {
        LambdaQueryWrapper<BilliardHall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BilliardHall::getCliUserId, cliUserId);
        wrapper.orderByDesc(BilliardHall::getId);
        wrapper.last("limit 1");
        BilliardHall billiardHall = baseMapper.selectOne(wrapper);
        //验证是否可以申请
        if (billiardHall != null) {
            Assert.whenTrueThrowEx(ApproveEnum.APPROVING.code.equals(billiardHall.getStatus()), "您已经提交过申请，审批中！");
        }
    }

    private BilliardHall checkCanExamine(Long id, String status, String refuseReason) {
        BilliardHall billiardHall = baseMapper.selectById(id);
        Assert.whenTrueThrowEx(billiardHall == null, "未获取到对应的数据！");
        Assert.whenTrueThrowEx(!(ObjectUtil.equal(status, ApproveEnum.APPROVED.code)
                || ObjectUtil.equal(status, ApproveEnum.REFUSED.code)), "状态异常！");
        Assert.whenTrueThrowEx(ObjectUtil.notEqual(billiardHall.getStatus(), ApproveEnum.APPROVING.code),
                "状态异常 ！");
        if (ApproveEnum.REFUSED.code.equals(status)) {
            Assert.whenTrueThrowEx(StringUtils.isBlank(refuseReason), "请填写未通过的原因！");
        }
        return billiardHall;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(Long id, String status, String reason) {
        BilliardHall billiardHall = checkCanExamine(id, status, reason);
        billiardHall.setStatus(status);
        Assert.whenTrueThrowEx(ApproveEnum.REFUSED.code.equals(status) && StringUtils.isBlank(reason), "请填写不通过的原因！");
        billiardHall.setRefuseReason(reason);
        baseMapper.updateById(billiardHall);
        if (ApproveEnum.APPROVED.code.equals(status)) {
            SysUser sysUser = null;
            //判断是否已经有对应的系统账户
            Long targetId = cliUserAuthService.isBilliardHall(billiardHall.getCliUserId());
            if (targetId != null) {
                sysUser = sysUserService.selectUserById(targetId);
            } else {
                //创建系统账号、绑定区域
                sysUser = new SysUser();
                sysUser.setUserName(billiardHall.getHallName());
                sysUser.setNickName(billiardHall.getHallName());
                sysUser.setPhonenumber(billiardHall.getHostPhone());
                sysUser.setUserType(UserTypeEnum.BILLIARD_HALL.getCode());
                sysUser.setAvatar(billiardHall.getMainImgUrl());
                if (!sysUserService.checkUserNameUnique(sysUser)) {
                    throw new ServiceException("生成系统账户异常，用户名【" + billiardHall.getHallName() + "】已存在！");
                }
                sysUserService.insertUser(sysUser);
                CliUserAuth cliUserAuth = new CliUserAuth();
                cliUserAuth.setTargetId(sysUser.getUserId());
                cliUserAuth.setUserType(UserTypeEnum.BILLIARD_HALL.getCode());
                cliUserAuth.setCliUserId(billiardHall.getCliUserId());
                cliUserAuthService.save(cliUserAuth);
            }
            billiardHall.setUserId(sysUser.getUserId());
            this.updateById(billiardHall);
        }
    }

    @Override
    public void fillBilliardHallInfo(WebHallFillRequest request) {
        BilliardHall billiardHall = this.getById(request.getId());
        Assert.whenTrueThrowEx(billiardHall == null, "未获取到对应球馆信息！");
        DozerUtil.map(request, billiardHall);
        this.updateById(billiardHall);
    }

    @Override
    public PageResDomain<WebBilliardHallInfoResp> page(WebHallPageReq req) {
        QueryWrapper<BilliardHall> wrapper = SearchTool.invoke(req);
        wrapper.lambda().eq(BilliardHall::getStatus, ApproveEnum.APPROVED.code);
        Page<BilliardHall> page = new Page<>(req.getCurrent(), req.getPageSize());
        page = baseMapper.selectPage(page, wrapper);
        if (page.getTotal() <= 0) {
            return new PageResDomain<>();
        }
        return renderPageResDomain(page);
    }

    private PageResDomain<WebBilliardHallInfoResp> renderPageResDomain(Page<BilliardHall> page) {
        PageResDomain<WebBilliardHallInfoResp> pageResDomain = PageResDomain.parse(page, WebBilliardHallInfoResp.class);
        pageResDomain.getRecords().forEach(r -> {
            Assert.doIfNotEmpty(r.getImgUrl(), () -> r.setImgUrls(Arrays.asList(StringUtils.split(r.getImgUrl(), "、"))));
        });
        return pageResDomain;
    }


    @Override
    public PageResDomain<WebBilliardHallInfoResp> examinePage(WebHallPageReq req) {
        QueryWrapper<BilliardHall> wrapper = SearchTool.invoke(req);
        Page<BilliardHall> page = new Page<>(req.getCurrent(), req.getPageSize());
        page = baseMapper.selectPage(page, wrapper);
        if (page.getTotal() <= 0) {
            return new PageResDomain<>();
        }
        return renderPageResDomain(page);
    }

    @Override
    public BilliardHall info() {
        Long wxUserId = WxUserUtils.current().getUserId();
        LambdaQueryWrapper<BilliardHall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BilliardHall::getCliUserId, wxUserId);
        wrapper.orderByDesc(BilliardHall::getId);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public WebBilliardHallInfoResp detail(Long id) {
        BilliardHall billiardHall = this.baseMapper.selectById(id);
        WebBilliardHallInfoResp resp = DozerUtil.map(billiardHall, WebBilliardHallInfoResp.class);
        Assert.doIfTrue(StringUtils.isNoneBlank(billiardHall.getImgUrl()), () ->
                resp.setImgUrls(Arrays.asList(StringUtils.split(billiardHall.getImgUrl(), "、")))
        );
        return resp;
    }

}
