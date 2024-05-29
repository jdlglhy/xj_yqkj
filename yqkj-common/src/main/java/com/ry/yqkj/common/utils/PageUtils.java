package com.ry.yqkj.common.utils;

import com.github.pagehelper.PageHelper;
import com.ry.yqkj.common.core.page.PageDomain;
import com.ry.yqkj.common.core.page.TableSupport;
import com.ry.yqkj.common.utils.sql.SqlUtil;

/**
 * 分页工具类
 * 
 * @author ry.yqkj
 */
public class PageUtils extends PageHelper
{
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
