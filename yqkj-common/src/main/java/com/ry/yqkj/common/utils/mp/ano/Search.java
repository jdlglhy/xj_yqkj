package com.ry.yqkj.common.utils.mp.ano;

import com.ry.yqkj.common.utils.mp.SearchType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Search {
    /**
     * 查询类型
     */
    SearchType type() default SearchType.EQ;

    /**
     * 判定字段名
     */
    String field() default "";

    /**
     * 是否忽略（忽略某个字段进行匹配查询）
     */
    boolean ignore() default false;

    /**
     * 是否另取 （）查询
     */
    boolean andNew() default false;

    /**
     * 用于多表关联时添加别名（别名必须与xml中定义的sql别名一致）
     */
    String tableAlias() default "";

    /**
     * 用于排序 true 表示该字段参与排序
     */
    boolean isSort() default false;

    /**
     * 用于升序和降序 默认降序
     */
    boolean asc() default false;

    /**
     * 多个排序用于指定排序顺序  数字越小排序越优先
     */
    int order() default 0;


}
