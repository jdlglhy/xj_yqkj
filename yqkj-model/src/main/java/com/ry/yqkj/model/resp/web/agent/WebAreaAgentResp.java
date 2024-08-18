package com.ry.yqkj.model.resp.web.agent;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author : lihy
 * @Description : 区域代理
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "area_agent")
public class WebAreaAgentResp {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 级别 ： 0 = 城市代理 、 1 = 省代理
     */
    private Integer level;

    /**
     * 用户ID
     */
    private Long cliUserId;
    /**
     * 系统用户ID 申请通过后会创建对应的系统用户
     */
    private Long userId;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 联系方式
     */
    private String contact;
    /**
     * 身份证反面
     */
    private String idCardBack;

    /**
     * 身份证 正面
     */
    private String idCardFront;

    /**
     * 其他附件信息
     */
    private String attachment;
    /**
     * 代理的城市
     */
    private String areaName;
    /**
     * 城市编码
     */
    private String areaCode;

    /**
     * 代理人邮箱
     */
    private String email;
    /**
     * 状态 approving（审批中）、approved（审批通过）、refused（未通过）
     */
    private String status;

    private String refuseReason;

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
