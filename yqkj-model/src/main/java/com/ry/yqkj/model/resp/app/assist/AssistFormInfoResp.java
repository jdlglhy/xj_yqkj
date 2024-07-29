package com.ry.yqkj.model.resp.app.assist;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : lihy
 * @Description : 助教申请单详情
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistFormInfoResp implements Serializable {
    /**
     * 审批单ID
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 费用（单位：元/小时）
     */
    private BigDecimal price;
    /**
     * 身份证正面
     */
    private String idCardFront;
    /**
     * 身份证反面
     */
    private String idCardBack;
    /**
     * 生活照
     */
    private List<String> lifePhotos;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 0 = 男 1 = 女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;
    /**
     * 微信
     */
    private String wx;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 所在 区、县
     */
    private String county;
    /**
     * 所在 街道、社区
     */
    private String street;
    /**
     * 门牌号信息
     */
    private String doorPlate;
    /**
     * 详细地址
     */
    private String lng;
    /**
     * 详细地址
     */
    private String lat;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 个性签名
     */
    private String perSign;

    /**
     * 审批状态(审批状态：approving（审批中）、approved（审批通过）、refused（未通过）)
     */
    private String approveState;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批意见
     */
    private String approvalOpinions;

    /**
     * 审批时间
     */
    private Date approvedTime;

    /**
     * 推荐码
     */
    private String recCode;
}
