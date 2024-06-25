package com.ry.yqkj.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.SecurityUtils;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.common.utils.mp.search.SearchTool;
import com.ry.yqkj.common.utils.uuid.SnowflakeIdUtil;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.req.app.assist.AssistApplyReq;
import com.ry.yqkj.model.req.app.assist.AssistPageReq;
import com.ry.yqkj.model.req.app.assist.AssistRecPageReq;
import com.ry.yqkj.model.req.web.assist.AssistFormExamReq;
import com.ry.yqkj.model.req.web.assist.AssistFormPageReq;
import com.ry.yqkj.model.resp.app.assist.AssistDetailResp;
import com.ry.yqkj.model.resp.app.assist.AssistFormInfoResp;
import com.ry.yqkj.model.resp.app.assist.AssistInfoResp;
import com.ry.yqkj.system.domain.AssistForm;
import com.ry.yqkj.system.domain.Assistant;
import com.ry.yqkj.system.domain.CliUser;
import com.ry.yqkj.system.mapper.app.AssistFormMapper;
import com.ry.yqkj.system.mapper.app.AssistantMapper;
import com.ry.yqkj.system.service.IAssistantService;
import com.ry.yqkj.system.service.ICliUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author : lihy
 * @Description : 助教 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class AssistantServiceImpl extends ServiceImpl<AssistantMapper, Assistant> implements IAssistantService {

    @Resource
    private AssistFormMapper assistFormMapper;
    @Resource
    private AssistantMapper assistantMapper;
    @Resource
    private ICliUserService cliUserService;


    @Override
    public void apply(AssistApplyReq assistApplyReq) {
        WxAppUser wxAppUser = WxUserUtils.current();
        CliUser cliUser = cliUserService.getById(wxAppUser.getUserId());
        //助教身份验证
        LambdaQueryWrapper<Assistant> assistWrap = new LambdaQueryWrapper<>();
        assistWrap.eq(Assistant::getCliUserId, cliUser.getId());
        Assistant assistant = assistantMapper.selectOne(assistWrap);
        if (assistant != null) {
            throw new ServiceException("不可申请！");
        }
        //判断是否审批中
        LambdaQueryWrapper<AssistForm> formWrap = new LambdaQueryWrapper<>();
        formWrap.eq(AssistForm::getCliUserId, wxAppUser.getUserId());
        formWrap.eq(AssistForm::getApproveState, ApproveEnum.APPROVING.code);
        AssistForm assistForm = assistFormMapper.selectOne(formWrap);
        if (assistForm != null) {
            throw new ServiceException("审批中、不可申请！");
        }
        //创建审批单
        AssistForm form = DozerUtil.map(assistApplyReq, AssistForm.class);
        form.setCliUserId(cliUser.getId());
        form.setNickName(StringUtils.isBlank(assistApplyReq.getNickName()) ? cliUser.getNickName() :
                assistApplyReq.getNickName());
        form.setCreateTime(new Date());
        form.setCreateBy(cliUser.getId() + "_" + cliUser.getNickName());
        //审批状态
        form.setApproveState(ApproveEnum.APPROVING.code);
        assistFormMapper.insert(form);
    }

    @Override
    public AssistFormInfoResp getLatestForm(Long userId) {
        LambdaQueryWrapper<AssistForm> formWrap = new LambdaQueryWrapper<>();
        formWrap.eq(AssistForm::getCliUserId, userId);
        formWrap.orderByDesc(AssistForm::getId);
        List<AssistForm> assistForm = assistFormMapper.selectList(formWrap);
        if (CollectionUtil.isEmpty(assistForm)) {
            return new AssistFormInfoResp();
        }
        return DozerUtil.map(assistForm.get(0), AssistFormInfoResp.class);
    }

    @Override
    public PageResDomain<AssistFormInfoResp> fromPage(AssistFormPageReq assistFormPageReq) {
        Page<AssistForm> page = new Page<>(assistFormPageReq.getCurrent(), assistFormPageReq.getPageSize());
        QueryWrapper<AssistForm> queryWrapper = SearchTool.invoke(assistFormPageReq);
        queryWrapper.lambda().orderByDesc(AssistForm::getCreateTime);
        page = assistFormMapper.selectPage(page, queryWrapper);
        return PageResDomain.parse(page, AssistFormInfoResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(AssistFormExamReq req) {
        if (!ApproveEnum.APPROVED.validate(req.getApproveState())) {
            throw new ServiceException("error，传入的状态错误！");
        }
        AssistForm assistForm = assistFormMapper.selectById(req.getFormId());
        if (assistForm == null) {
            throw new ServiceException("error，未找到对应的数据！");
        }
        if (ObjectUtils.notEqual(ApproveEnum.APPROVING.code, assistForm.getApproveState())) {
            throw new ServiceException("error，状态异常！");
        }
        assistForm.setApproveState(req.getApproveState());
        assistForm.setApprovedTime(new Date());
        assistForm.setModifyTime(new Date());
        assistForm.setApprovalOpinions(req.getReason());
        assistForm.setModifyBy(SecurityUtils.getUserId() + "_" + SecurityUtils.getUsername());
        assistForm.setApprover(assistForm.getModifyBy());
        if (ApproveEnum.REFUSED.code.equals(req.getApproveState())) {
            if (StringUtils.isBlank(assistForm.getApprovalOpinions())) {
                throw new ServiceException("请填写审批意见！");
            }
            assistFormMapper.updateById(assistForm);
            return;
        }
        assistFormMapper.updateById(assistForm);
        //审批通过、创建助教
        Assistant assistant = DozerUtil.map(assistForm, Assistant.class);
        assistant.setId(SnowflakeIdUtil.nextId());
        assistant.setModifyBy("");
        assistant.setModifyTime(null);
        assistant.setCreateTime(new Date());
        assistant.setCreateBy(assistForm.getApprover());
        assistantMapper.insert(assistant);
    }

    @Override
    public PageResDomain<AssistInfoResp> assistPage(AssistPageReq assistPageReq) {
        Page<Assistant> page = new Page<>(assistPageReq.getCurrent(), assistPageReq.getPageSize());
        QueryWrapper<Assistant> queryWrapper = SearchTool.invoke(assistPageReq);
        page = assistantMapper.selectPage(page, queryWrapper);
        return PageResDomain.parse(page, AssistInfoResp.class);
    }

    @Override
    public PageResDomain<AssistInfoResp> assistRecPage(AssistRecPageReq assistRecPageReq) {
        AssistPageReq assistPageReq = DozerUtil.map(assistRecPageReq, AssistPageReq.class);
        return this.assistPage(assistPageReq);
    }

    @Override
    public AssistDetailResp assistDetail(Long assistId) {
        Assistant assistant = assistantMapper.selectById(assistId);
        if (assistant == null) {
            throw new ServiceException("信息不存在啦！");
        }
        return DozerUtil.map(assistant, AssistDetailResp.class);
    }
}
