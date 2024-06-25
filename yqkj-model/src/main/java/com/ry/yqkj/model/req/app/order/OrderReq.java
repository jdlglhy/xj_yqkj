package com.ry.yqkj.model.req.app.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author : lihy
 * @Description : 下单请求参数
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class OrderReq implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 助教ID
     */
    @ApiModelProperty(value = "助教ID集合", required = true)
    @NotEmpty(message = "助教ID集合不能为空")
    @Size(max = 5,message = "最多只能选择5个")
    private Set<Long> assistIdSet;

    /**
     * 预约时长 单位 H（小时）
     */
    @ApiModelProperty(value = "预约时长（H）", required = true)
    @NotNull(message = "预约时长")
    private BigDecimal reserveDur;
    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时长（H）", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "预约时间不能为空")
    private LocalDateTime reserveTime;
    /**
     * 球馆名称
     */
    @ApiModelProperty(value = "球馆名称", required = true)
    @NotBlank(message = "球馆名称不能为空")
    @Length(max = 64)
    private String ballRoomName;

    /**
     * 球馆地址
     */
    @ApiModelProperty(value = "球馆地址", required = true)
    @NotBlank(message = "球馆地址不能为空")
    @Length(max = 128)
    private String serviceAddress;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度", required = false)
    private String lng;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度", required = false)
    private String lat;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", required = true)
    @Length(max = 128)
    private String remark;
}
