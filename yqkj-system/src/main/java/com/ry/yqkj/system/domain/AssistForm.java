package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 助教申请单
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "app_assist_form")
public class AssistForm implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long cliUserId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 费用
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
    private String lifePhoto;
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
     * 详细地址
     */
    private String address;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvedTime;

    /**
     * 推荐码
     */
    private String recCode;

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
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    /**
     * 修改人
     */
    private String modifyBy;
}
