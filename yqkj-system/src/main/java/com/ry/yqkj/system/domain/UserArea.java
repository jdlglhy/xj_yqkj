package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 用户区域权限
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "user_area")
public class UserArea implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 级别 ： 0 = 城市代理 、 1 = 省代理
     */
    private Integer level;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域code
     */
    private String areaCode;


}
