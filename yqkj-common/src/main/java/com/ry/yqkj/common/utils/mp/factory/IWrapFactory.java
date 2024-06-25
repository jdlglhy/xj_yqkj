package com.ry.yqkj.common.utils.mp.factory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface IWrapFactory {
    /**
     * 组装查询类型
     */
    void buildWrap(QueryWrapper<?> wrapper, String field, Object value);

}
