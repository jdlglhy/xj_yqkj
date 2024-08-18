package com.ry.yqkj.model.resp.web.assist;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : lihy
 * @Description : 助教信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebAssistInfoResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 助教ID
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 费用（单位：元/小时）
     */
    private BigDecimal price;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 0 = 男 1 = 女
     */
    private Integer gender;
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
     * 街道、社区
     */
    private String street;

    /**
     * 状态 online = 在线 offline = 离线
     */
    private String status;

    /**
     * 个性签名
     */
    private String perSign;
}
