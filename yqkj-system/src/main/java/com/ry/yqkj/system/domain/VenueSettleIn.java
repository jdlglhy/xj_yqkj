package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author : lihy
 * @Description : 场馆入住
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "venue_settle_in")
public class VenueSettleIn {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 球馆名称
     */
    private String name;
    /**
     * 球馆图片
     */
    private String image;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 营业时间
     */
    private String businessHour;

    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 球馆电话
     */
    private String tel;
    /**
     * 维度
     */
    private String lat;
    /**
     * 精度
     */
    private String lng;

    private String province;
    private String city;
    private String county;
    /**
     * 球馆地址（省市区 + 详细地址）
     */
    private String address;
    /**
     * 备注信息
     */
    private String remark;

    /**
     * 状态 approving（审批中）、approved（审批通过）、refused（未通过）
     */
    private String status;

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
