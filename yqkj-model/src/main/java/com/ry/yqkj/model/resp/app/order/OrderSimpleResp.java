package com.ry.yqkj.model.resp.app.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author : lihy
 * @Description : 订单基础 信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderSimpleResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 服务订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long cliUserId;
    /**
     * 用户昵称
     */
    private String cliUserNickName;
    /**
     * 助教ID
     */
    private Long assistId;
    /**
     * 助教昵称
     */
    private String assistNickName;

    /**
     * 助教头像
     */
    private String assistAvatar;
    /**
     * 助教性别
     */
    private Integer assistGender;

    /**
     * 预约时长 单位 H（小时）
     */
    private BigDecimal reserveDur;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 预约时间
     */
    private LocalDateTime reserveTime;

    /**
     * 订单状态 订单状态 state  已下单（ordered）、已取消（canceled）、已支付/待服务（paid）、服务中（servicing）、已完成（done）
     */
    private String status;
    /**
     * 邀约状态（W = 已邀约  A= 已接单 R = 拒绝（取消订单） ）
     */
    private String inviteStatus;
    /**
     * 评价状态 ： N = 未评价（默认） Y=已评价
     */
    private String evalStatus;
    /**
     * 球馆名称
     */
    private String ballRoomName;

    /**
     * 球馆地址
     */
    private String serviceAddress;
    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;

    /**
     * 拒绝接单原因
     */
    private String refuseReason;

    /**
     * U = 用户取消 A = 助教取消
     */
    private String cancelFrom;


    /**
     * 用户备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;

    /**
     * 助教标签、星级信息
     */
    private AssistEval assistEval;

    @Data
    public static class AssistEval implements Serializable {
        private BigDecimal score;
        private List<String> tags;
    }
}
