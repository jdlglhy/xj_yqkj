package com.ry.yqkj.model.resp.app.billiardhall;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 球馆详情
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AppBilliardHallInfoResp implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
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
     * 备注
     */
    private String remark;
    /**
     * 审核不通过原因
     */
    private String refuseReason;
}
