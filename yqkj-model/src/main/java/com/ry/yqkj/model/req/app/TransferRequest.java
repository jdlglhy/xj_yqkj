package com.ry.yqkj.model.req.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author : lihy
 * @Description : todo
 * @date : 2025/3/1 10:09 下午
 */
@Data
public class TransferRequest implements Serializable {
    /**
     * 小程序AppID
     */
    @JsonProperty("appid")
    private String appid;



    /**
     * 商户批次号
     */
    @JsonProperty("out_batch_no")
    private String outBatchNo;

    /**
     * 批次名称
     */
    @JsonProperty("batch_name")
    private String batchName;

    /**
     * 批次备注
     */
    @JsonProperty("batch_remark")
    private String batchRemark;

    /**
     * 总金额（单位：分）
     */
    @JsonProperty("total_amount")
    private int totalAmount;

    /**
     * 总笔数
     */
    @JsonProperty("total_num")
    private int totalNum;

    /**
     * 转账明细
     */
    @JsonProperty("transfer_detail_list")
    private List<TransferDetail> transferDetailList;

    @Data
    public static class TransferDetail {

        /**
         * 商户明细单号
         */
        @JsonProperty("out_detail_no")
        private String outDetailNo;

        /**
         * 转账金额（单位：分）
         */
        @JsonProperty("transfer_amount")
        private int transferAmount;

        /**
         * 转账备注
         */
        @JsonProperty("transfer_remark")
        private String transferRemark;

        /**
         * 用户OpenID
         */
        @JsonProperty("openid")
        private String openid;
    }
}
