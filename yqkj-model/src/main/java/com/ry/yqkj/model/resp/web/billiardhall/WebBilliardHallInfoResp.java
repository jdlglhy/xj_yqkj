package com.ry.yqkj.model.resp.web.billiardhall;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : lihy
 * @Description : 球馆入驻
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebBilliardHallInfoResp implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 客户端ID
     */
    private Long cliUserId;
    /**
     * 绑定系统用户ID
     */
    private Long userId;
    /**
     * 球馆名称
     */
    private String hallName;
    /**
     * 球馆电话
     */
    private String hallContact;
    /**
     * 球馆负责人
     */
    private String hostName;
    /**
     * 球馆负责人手机
     */
    private String hostPhone;

    /**
     * 营业时间
     */
    private String bizHour;
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
     * 审批状态
     */
    private String status;

    /**
     * 详细地址
     */
    private String address;
    /**
     * 球馆主图
     */
    private String mainImgUrl;
    /**
     * 球馆风采图片 多个用、号拼接
     */
    private List<String> imgUrls;
    /**
     * 球馆风采图片 多个用、号拼接
     */
    private String imgUrl;
    /**
     * 备注
     */
    private String remark;
    /**
     * 审核不通过原因
     */
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
