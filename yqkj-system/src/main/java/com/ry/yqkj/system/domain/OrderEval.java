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
 * @Description : 评价
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "order_eval")
public class OrderEval implements Serializable {

    private static final long serialVersionUID = -1L;
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
     * 助教ID
     */
    private Long assistId;
    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 是否满意 1 = 满意，0 = 不满意
     */
    private Integer satisfied;
    /**
     * 总分（不满意=0，满意=10，+专业+时效+服务态度）
     */
    private BigDecimal score;
    /**
     * 专业性
     */
    private Integer speciality;
    /**
     * 时效性
     */
    private Integer timeLiness;

    /**
     * 服务态度
     */
    private Integer serviceAttitude;

    /**
     * 评价内容
     */
    private String content;
    /**
     * 标签多个、分隔
     */
    private String tag;
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
