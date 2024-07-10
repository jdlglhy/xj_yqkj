package com.ry.yqkj.model.resp.app.assist;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author : lihy
 * @Description : 助教详情
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistDetailResp implements Serializable {

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
     * 费用
     */
    private BigDecimal price;

    /**
     * 可预约时间 （10：00-14：00 多个逗号拼接）
     */
    private String reservePeriod;
    /**
     * 生活照
     */
    private List<String> lifePhotos;
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
     * 所在 街道、社区
     */
    private String street;
    /**
     * 状态
     */
    private String status;
    /**
     * 个性签名
     */
    private String perSign;
}
