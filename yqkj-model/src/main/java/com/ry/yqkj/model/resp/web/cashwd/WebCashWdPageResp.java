package com.ry.yqkj.model.resp.web.cashwd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : lihy
 * @Description : 提现管理
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebCashWdPageResp implements Serializable {

    private Long id;
    /**
     * 用户ID
     */
    private Long accountId;
    /**
     * 银行卡号
     */
    private String bankCard;
    /**
     * 银行类型
     */
    private String bankType;
    /**
     * 收款方
     */
    private String payee;
    /**
     * 手机号
     */
    private String phone;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 转账凭证
     */
    private String attach;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：processing = 处理中、done = 完成、expired = 已失效
     */
    private String status;
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
