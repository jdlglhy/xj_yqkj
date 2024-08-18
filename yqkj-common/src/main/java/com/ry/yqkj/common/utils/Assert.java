package com.ry.yqkj.common.utils;

import com.ry.yqkj.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 简化代码工具类
 */
@Slf4j
public class Assert {

    /**
     * condition 为true 执行action
     *
     * @param condition 条件
     * @param action    方法
     */
    public static void doIfTrue(boolean condition, Runnable action) {
        if (condition) {
            action.run();
        }
    }

    /**
     * object对象不为空的时候 执行action
     *
     * @param object 对象
     * @param action 方法
     */
    public static void doIfNotNull(Object object, Runnable action) {
        doIfTrue(Objects.nonNull(object), action);
    }

    /**
     * str字符串不为空的时候 执行action
     *
     * @param str    字符串
     * @param action 方法
     */
    public static void doIfNotEmpty(String str, Runnable action) {
        doIfTrue(StringUtils.isNoneBlank(str), action);
    }

    /**
     * collection 集合不为空的时候 执行action
     *
     * @param collection 集合
     * @param action     方法
     */
    public static void doIfNotEmpty(Collection<?> collection, Runnable action) {
        doIfTrue(CollectionUtils.isNotEmpty(collection), action);
    }

    /**
     * condition 为true执行 tRun，否则执行fRun
     *
     * @param condition 条件
     * @param tRun      方法
     * @param fRun      方法
     */
    public static void doIfHandle(boolean condition, Runnable tRun, Runnable fRun) {
        if (condition) {
            tRun.run();
        } else {
            fRun.run();
        }
    }

    /**
     * condition 为true 抛对应的异常
     *
     * @param condition 条件
     * @param message   异常信息
     */
    public static void whenTrueThrowEx(boolean condition, String message) {
        if (condition) {
            throw new ServiceException(message);
        }
    }
}
