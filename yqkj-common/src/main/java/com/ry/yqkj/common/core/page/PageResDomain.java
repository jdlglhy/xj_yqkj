package com.ry.yqkj.common.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ry.yqkj.common.utils.DozerUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResDomain<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> records;
    private Long total;
    private Long pageSize;
    private Long current;

    /**
     * 解析
     *
     * @param srcPage       mybatisplu的分页对象
     * @param destItemClass 生成的分页列表对象类
     * @param <S>
     * @param <T>
     * @return 分页响应类
     */
    public static <S, T> PageResDomain<T> parse(IPage<S> srcPage, Class<T> destItemClass) {
        PageResDomain<T> destPage = new PageResDomain<>();
        destPage.setCurrent(srcPage.getCurrent());
        destPage.setPageSize(srcPage.getSize());
        destPage.setTotal(srcPage.getTotal());
        destPage.setRecords(Lists.newArrayList());
        if (srcPage.getTotal() > 0) {
            destPage.setRecords(DozerUtil.mapList(srcPage.getRecords(), destItemClass));
        }
        return destPage;
    }

//    /**
//     * 解析
//     *
//     * @param srcPage       mybatisplu的分页对象
//     * @param destItemClass 生成的分页列表对象类
//     * @param <S>
//     * @param <T>
//     * @return 分页响应类
//     */
//    public static <S, T> PageResDomain<T> parse(IPage<S> srcPage, Class<T> destItemClass) {
//        return parse(srcPage, destItemClass);
//    }
//
//    /**
//     * 解析
//     *
//     * @param srcPage        来源分页响应类
//     * @param destItemClass  生成的分页列表对象类
//     * @param customConsumer 自定义分页列表对象转换
//     * @param <S>
//     * @param <T>
//     * @return 分页响应类
//     */
//    public static <S, T> PageResDomain<T> parse(PageResDomain<S> srcPage, Class<T> destItemClass) {
//        PageResDomain<T> destPage = new PageResDomain<>();
//        destPage.setCurrent(srcPage.getCurrent());
//        destPage.setPageSize(srcPage.getPageSize());
//        destPage.setTotal(srcPage.getTotal());
//        destPage.setRecords(BeanUtil.copyToList(srcPage.getRecords(), destItemClass));
//        return destPage;
//    }
//
//    /**
//     * 解析
//     *
//     * @param srcPage       来源分页响应类
//     * @param destItemClass 生成的分页列表对象类
//     * @param <S>
//     * @param <T>
//     * @return 分页响应类
//     */
//    public static <S, T> PageResDomain<T> parse(PageResDomain<S> srcPage, Class<T> destItemClass) {
//        return parse(srcPage, destItemClass);
//    }


}
