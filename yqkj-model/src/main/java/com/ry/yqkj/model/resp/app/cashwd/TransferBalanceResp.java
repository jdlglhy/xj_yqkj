package com.ry.yqkj.model.resp.app.cashwd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 转账接口返回信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
@Builder
public class TransferBalanceResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * package参数
     */
    @JsonProperty("package")
    private String packageInfo;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * appId
     */
    private String appId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("out_bill_no")
    private String outBillNo;
    @JsonProperty("transfer_bill_no")
    private String transferBillNo;
}
