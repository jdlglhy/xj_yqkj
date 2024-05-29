package com.ry.yqkj.common.utils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/***
 * @title DozerUtils
 * @description dozer 对象拷贝
 * copy from https://blog.csdn.net/weixin_71263927/article/details/127548357
 *
 * @author lixy
 * @data 2023/7/7 13:40
 **/
public class DozerUtil {

    private final static Mapper dozerMapper = DozerBeanMapperBuilder.buildDefault();

    /**
     * 构造新的destinationClass实例对象，通过source对象中的字段内容
     * 映射到destinationClass实例对象中，并返回新的destinationClass实例对象。
     *
     * @param source           源数据对象
     * @param destinationClass 要构造新的实例对象Class
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozerMapper.map(source, destinationClass);
    }

    /**
     * 将对象source的所有属性值拷贝到对象destination中.
     *
     * @param source            对象source
     * @param destinationObject 对象destination
     */
    public static void map(Object source, Object destinationObject) {
        dozerMapper.map(source, destinationObject);
    }

    /**
     * List数据源映射
     *
     * @param sourceList
     * @param destinationClass
     * @param <T>
     * @return List<T>
     */
    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List destinationList = new ArrayList<>();
        for (Iterator iterator = sourceList.iterator(); iterator.hasNext(); ) {
            Object sourceObject = iterator.next();
            Object destinationObject = dozerMapper.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }
}
