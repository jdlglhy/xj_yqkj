package com.ry.yqkj.model.req.web.cashwd;

import com.ry.yqkj.common.core.page.PageReqDomain;
import lombok.Data;

/**
 * @author : lihy
 * @Description : 提现分页列表
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class WebCashWdPageReq extends PageReqDomain {

    private static final long serialVersionUID = -1L;


    /**
     * 状态
     */
    private String status;

}
