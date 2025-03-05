package com.ry.yqkj.model.req.app;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author : lihy
 * @Description : todo
 * @date : 2025/3/1 10:09 下午
 */
@Data
@Slf4j
public class TransferV2Request implements Serializable {
    /**
     * 小程序AppID
     */
    @JsonProperty("appid")
    private String appId;

    @JsonProperty("openid")
    private String openId;

    /**
     * 商户批次号
     */
    @JsonProperty("out_bill_no")
    private String outBillNo;

    /**
     * 场景
     */
    @JsonProperty("transfer_scene_id")
    private String transferSceneId;

    /**
     * 转账金额
     */
    @JsonProperty("transfer_amount")
    private int transferAmount;

    /**
     * 转账金额 备注
     */
    @JsonProperty("transfer_remark")
    private String transferRemark;



    @JsonProperty("user_recv_perception")
    private String userRecvPerception;

    /**
     * 转账明细
     */
    @JsonProperty("transfer_scene_report_infos")
    private List<transferSceneReportInfo> transferSceneReportInfos;

    @Data
    public static class transferSceneReportInfo {


        @JsonProperty("info_type")
        private String infoType;


        @JsonProperty("info_content")
        private String infoContent;

    }


    public static void main(String[] args) {
        List<transferSceneReportInfo> transferSceneReportInfos = Lists.newArrayList();
        transferSceneReportInfo info = new transferSceneReportInfo();
        info.setInfoType("活动名称");
        info.setInfoContent("清台挑战");

        transferSceneReportInfo info2 = new transferSceneReportInfo();
        info2.setInfoType("奖励说明");
        info2.setInfoContent("佣金报酬");
        transferSceneReportInfos.add(info);
        transferSceneReportInfos.add(info2);



        TransferV2Request request = new TransferV2Request();
        request.setAppId("eere");
        request.setOpenId("sdfsdf");
        request.setTransferAmount(10);
        request.setTransferRemark("sdf");
        request.setTransferSceneId("sdf");
        request.setOutBillNo("sdfsdfdf");
        request.setTransferSceneReportInfos(transferSceneReportInfos);

        log.info("request={}", JSON.toJSON(request));
    }


}
