package com.ry.yqkj.model.resp.app.cliuser;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author : lihy
 * @Description : 评价返回参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderEvalResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 是否满意 1 = 满意，0 = 不满意
     */
    private Integer satisfied;
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

    private BigDecimal score;

    /**
     * 评价内容
     */
    private String content;
    /**
     * 标签多个、分隔
     */
    private String tag;
    /**
     * 标签多个、分隔
     */
    private List<String> tags;

    /**
     * 用户信息
     */
    private CliUserInfoResp cliUserInfoResp;
}
