package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 助教
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "app_assistant")
public class Assistant implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 可预约时间 （10：00-14：00 多个逗号拼接）
     */
    private String reservePeriod;
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
     * 状态
     */
    private String status;

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
