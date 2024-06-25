package com.ry.yqkj.common.utils.mp.search;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.ry.yqkj.common.utils.mp.SearchType;
import com.ry.yqkj.common.utils.mp.ano.Search;
import com.ry.yqkj.common.utils.mp.factory.IWrapFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SearchTool {
    private static final Logger logger = LoggerFactory.getLogger(SearchTool.class);
    /**
     * 查询条件转化器
     */
    private static final Map<SearchType, IWrapFactory> searchWrapFactory = new HashMap<>();

    /**
     * 驼峰转下划线
     */
    private static final Converter<String, String> converter =
            CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    static {
        searchWrapFactory.put(SearchType.EQ, Compare::eq);
        searchWrapFactory.put(SearchType.NE, Compare::ne);
        searchWrapFactory.put(SearchType.IN, (w, k, v) -> {
            if (v instanceof Collection) {
                w.in(k, (Collection<?>)v);
            } else if (v instanceof Object[]) {
                w.in(k, (Object[])v);
            } else {
                w.in(k, v.toString());
            }
        });
        searchWrapFactory.put(SearchType.NOT_IN, (w, k, v) -> {
            if (v instanceof Collection) {
                w.notIn(k, (Collection<?>)v);
            } else if (v instanceof Object[]) {
                w.notIn(k, (Object[])v);
            } else {
                w.notIn(k, v.toString());
            }
        });
        searchWrapFactory.put(SearchType.LIKE, (w, k, v) -> {
            w.like(k, v.toString());
        });
        searchWrapFactory.put(SearchType.LE, Compare::le);
        searchWrapFactory.put(SearchType.LT, Compare::lt);
        searchWrapFactory.put(SearchType.GE, Compare::ge);
        searchWrapFactory.put(SearchType.GT, Compare::gt);
    }

    /**
     * 封装成需要的wrapper
     *
     * @param queryParam 查询参数对象
     * @return QueryWrapper
     */
    public static <E> QueryWrapper<E> invoke(Object queryParam) {
        QueryWrapper<E> wrapper = new QueryWrapper<>();
        execute(queryParam, wrapper);
        return wrapper;
    }

    public static <E> void invoke(Object queryParam, QueryWrapper<E> wrapper) {
        execute(queryParam, wrapper);
    }

    /**
     * 执行
     *
     * @param queryParam 查询参数对象
     * @param wrapper    QueryWrapper
     */
    public static <E> void execute(Object queryParam, QueryWrapper<E> wrapper) {
        // 需要进行orderBy
        List<OrderItem> sortLists = Lists.newArrayList();
        // 反射获取属性
        Field[] fields = queryParam.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 忽略静态字段，如 serialVersionUID
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                field.setAccessible(true);
                Search search = field.getAnnotation(Search.class);
                String column = getColumn(field, search);
                if (StringUtils.isBlank(column)) {
                    continue;
                }
                Object val = field.get(queryParam);
                SearchType searchType = search != null ? search.type() : SearchType.EQ;
                if (ObjectUtils.isNotEmpty(val)) {
                    searchWrapFactory.get(searchType).buildWrap(wrapper, column, val);
                }
                builderOrderItem(sortLists, column, search);
            } catch (IllegalAccessException e) {
                logger.error("queryParam:{} field:{} @Search wrap error", queryParam, field, e);
            }
        }
        sortLists.forEach(s -> {
            wrapper.orderBy(true, s.isAsc(), s.getColumn());
        });
    }

    /**
     * 获取字段
     */
    private static String getColumn(Field field, Search search) {
        // 没有注解，取默认为下划线拼接
        if (search == null) {
            return converter.convert(field.getName());
        }
        if (search.ignore() && !search.isSort()) {
            return null;
        }
        String column = "";
        String alias = search.tableAlias();
        // 没有定义查询属性，取默认
        if (!StringUtils.isBlank(search.field())) {
            column = search.field();
        } else {
            column = converter.convert(field.getName());
        }
        if (StringUtils.isNotBlank(alias)) {
            column = alias + "." + column;
        }
        return column;
    }

    /**
     * 获取排序支持
     */
    private static void builderOrderItem(List<OrderItem> orderItems, String column, Search search) {
        if (search != null && search.isSort()) {
            orderItems.add(new OrderItem(column, search.asc(), search.order()));
        }
    }

    /**
     * 排序实体
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class OrderItem implements Comparable<OrderItem> {
        /**
         * 需要排序的字段
         */
        private String column;
        /**
         * 是否升序 false默认升序
         */
        private boolean asc;
        /**
         * 多个排序字段用于指定排序的顺序
         */
        private int order;

        @Override
        public int compareTo(OrderItem obj) {
            return this.order - obj.getOrder();
        }
    }

}
