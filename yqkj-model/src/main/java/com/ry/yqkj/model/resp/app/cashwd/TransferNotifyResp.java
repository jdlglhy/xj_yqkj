package com.ry.yqkj.model.resp.app.cashwd;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 转账回调（用户确认收款后来自微信回调的信息）
 * <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012712115">...</a>
 * @date : 2024/5/19 11:14 下午
 */
@Data
@Builder
public class TransferNotifyResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 商户号
     */
    @SerializedName("mch_id")
    private String mchId;
    @SerializedName("openId")
    private String openId;

    @SerializedName("transfer_amount")
    private int transferAmount;
    /**
     * 【单据状态】微信单号，微信商家转账系统返回的唯一标识
     * ACCEPTED：单据已受理
     * PROCESSING：单据处理中，转账结果尚未明确，如一直处于此状态，建议检查账户余额是否足够
     * WAIT_USER_CONFIRM：待收款用户确认，可拉起微信收款确认页面进行收款确认
     * TRANSFERING：转账中，转账结果尚未明确，可拉起微信收款确认页面再次重试确认收款
     * SUCCESS： 转账成功
     * FAIL： 转账失败
     * CANCELING： 撤销中
     * CANCELLED： 已撤销
     */
    @SerializedName("state")
    private String state;
    /**
     * 【单据状态】商户转账单号
     */
    @SerializedName("out_bill_no")
    private String outBillNo;

    /**
     * 【单据状态】微信单号，微信商家转账系统返回的唯一标识
     */
    @SerializedName("transfer_bill_no")
    private String transferBillNo;

    @SerializedName("fail_reason")
    private String failReason;

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("update_time")
    private String updateTime;

}
