package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 用户代理区域管理
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "user_agent_area")
public class UserAgentArea {

    /**
     * 代理ID
     */
    private Long userId;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 代理区域名称（城市）
     */
    private String areaName;

    /**
     * 佣金比例
     */
    private BigDecimal commission;

    /**
     * 邀请码信息
     */
    private String code;

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
