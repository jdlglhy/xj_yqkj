package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.cashwd.CashWdPageReq;
import com.ry.yqkj.model.req.app.cashwd.CashWdReq;
import com.ry.yqkj.model.resp.app.cashwd.CashWdInfoResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferBalanceResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferNotifyResp;
import com.ry.yqkj.system.domain.CashWithdraw;

/**
 * @author : lihy
 * @Description : 提现管理 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface ICashWdService extends IService<CashWithdraw> {


    /**
     * 提现申请
     *
     * @param req 提现申请参数
     */
    TransferBalanceResp cashWdApply(CashWdReq req) throws Exception;


    /**
     * 提现申请记录
     *
     * @param page 提现申请记录
     */
    PageResDomain<CashWdInfoResp> cashPageRecord(CashWdPageReq page);


    /**
     * 获取用户再次确认收款的package参数
     *
     * @return TransferBalanceResp
     */
    TransferBalanceResp getUserConfirmPackage(Long id);


    /**
     * 转账成功回调
     */
    void notifySuccess(TransferNotifyResp resp);
}
