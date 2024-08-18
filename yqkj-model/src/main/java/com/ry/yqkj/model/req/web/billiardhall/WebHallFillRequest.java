package com.ry.yqkj.model.req.web.billiardhall;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author : lihy
 * @Description : 完善球馆信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebHallFillRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 球馆ID
     */
    @NotNull(message = "球馆ID")
    private Long id;
    /**
     * 球馆电话
     */
    @NotBlank(message = "球馆联系方式")
    private String hallContact;
    /**
     * 负责人
     */
    @NotBlank(message = "负责人")
    @Length(max = 16, message = "负责人姓名不能超过16个字符")
    private String hostName;
    /**
     * 球馆电话
     */
    @NotBlank(message = "负责人联系方式")
    @Length(max = 16, message = "负责人联系方式不能超过16个字符")
    private String hostPhone;
    /**
     * 省
     */
    @NotBlank(message = "所在省份参数缺失")
    private String province;
    /**
     * 市
     */
    @NotBlank(message = "所在城市参数缺失")
    private String city;
    /**
     * 所在 区、县
     */
    @NotBlank(message = "所在县、区域参数缺失")
    private String county;
    /**
     * 门牌号信息
     */
    private String doorPlate;
    /**
     * 经度
     */
    @NotBlank(message = "经度参数缺失")
    private String lng;
    /**
     * 维度
     */
    @NotBlank(message = "纬度参数缺失")
    private String lat;

    /**
     * 详细地址
     */
    @NotBlank(message = "请填写详细地址")
    @Length(max = 64, message = "详细地址64个字符")
    private String address;
    /**
     * 备注
     */
    @Length(max = 64, message = "备注64个字符")
    private String remark;
    /**
     * 球馆主图
     */
    private String mainImgUrl;
    /**
     * 球馆风采图片 多个用、号拼接
     */
    private List<String> imgUrls;
}
