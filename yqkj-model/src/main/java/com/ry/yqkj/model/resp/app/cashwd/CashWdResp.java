package com.ry.yqkj.model.resp.app.cashwd;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 提现返回信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CashWdResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * package参数
     */
    private String packageInfo;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * appId
     */
    private String appId;
}
