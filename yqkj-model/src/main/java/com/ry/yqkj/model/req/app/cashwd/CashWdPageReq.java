package com.ry.yqkj.model.req.app.cashwd;

import com.ry.yqkj.common.core.page.PageReqDomain;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import lombok.Data;

/**
 * @author : lihy
 * @Description : 提现分页列表
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CashWdPageReq extends PageReqDomain {

    private static final long serialVersionUID = -1L;

    /**
     * 收款方
     */
    @Search(type = SearchType.LIKE)
    private String payee;

    /**
     * 手机号
     */
    @Search(type = SearchType.LIKE)
    private String phone;

    /**
     * 银行卡号
     */
    @Search(type = SearchType.LIKE)
    private String bankNo;

}
