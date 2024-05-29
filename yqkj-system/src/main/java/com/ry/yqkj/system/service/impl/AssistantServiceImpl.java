package com.ry.yqkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.core.domain.model.WxAppUser;
import com.ry.yqkj.common.exception.ServiceException;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.common.utils.WxUserUtils;
import com.ry.yqkj.model.enums.ApproveEnum;
import com.ry.yqkj.model.req.assist.AssistApplyReq;
import com.ry.yqkj.model.resp.assist.AssistFormInfoResp;
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
        assistWrap.eq(Assistant::getCliUserId, cliUser.getCity());
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
        form.setCliUserId(cliUser.getWxUserId());
        form.setNickName(StringUtils.isBlank(assistApplyReq.getNickName()) ? cliUser.getNickName() :
                assistApplyReq.getNickName());
        form.setCreateTime(new Date());
        form.setCreateBy(cliUser.getId() + "_" + cliUser.getNickName());
        //审批状态
        form.setApproveState(ApproveEnum.APPROVING.code);
        assistFormMapper.insert(form);
    }

    @Override
    public AssistFormInfoResp getLatestForm() {
        WxAppUser wxAppUser = WxUserUtils.current();
        LambdaQueryWrapper<AssistForm> formWrap = new LambdaQueryWrapper<>();
        formWrap.eq(AssistForm::getCliUserId, wxAppUser.getUserId());
        formWrap.eq(AssistForm::getApproveState, ApproveEnum.APPROVING.code);
        formWrap.orderByDesc(AssistForm::getCreateBy);
        AssistForm assistForm = assistFormMapper.selectOne(formWrap);
        if (assistForm == null) {
            return new AssistFormInfoResp();
        }
        return DozerUtil.map(assistForm, AssistFormInfoResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void examine(Long formId, String approveState, String reason) {
        AssistForm assistForm = assistFormMapper.selectById(formId);
        if (assistForm == null) {
            throw new ServiceException("error，未找到对应的数据！");
        }
        if (ObjectUtils.notEqual(ApproveEnum.APPROVING.code, assistForm.getApproveState())) {
            throw new ServiceException("error，状态异常！");
        }
        //审批通过、创建助教
        //if(){


        //}

    }
}
