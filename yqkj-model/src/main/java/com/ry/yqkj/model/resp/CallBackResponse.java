package com.ry.yqkj.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * since: 2025/3/6 20:53
 * author: lihy
 * description: 微信支付、退款、转账回调
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBackResponse {
    private String code;
    private String message;
}
