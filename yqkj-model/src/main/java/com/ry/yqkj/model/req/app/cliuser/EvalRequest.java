package com.ry.yqkj.model.req.app.cliuser;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author : lihy
 * @Description : 评价请求参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class EvalRequest implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 订单号
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 是否满意 1 = 满意，0 = 不满意
     */
    @NotNull(message = "清选择满意与否")
    @Max(1)
    @Min(0)
    private Integer satisfied = 1;
    /**
     * 专业性
     */
    @Max(5)
    @Min(1)
    @NotNull(message = "请进行专业性评价")
    private Integer speciality = 5;
    /**
     * 时效性
     */
    @Max(5)
    @Min(1)
    @NotNull(message = "请进行时效性评价")
    private Integer timeLiness = 5;

    /**
     * 服务态度
     */
    @Max(5)
    @Min(1)
    @NotNull(message = "请进行服务态度评价")
    private Integer serviceAttitude = 5;

    /**
     * 评价内容
     */
    @Length(max = 64)
    private String content;
    /**
     * 标签多个、分隔
     */
    private List<String> tags;
}
