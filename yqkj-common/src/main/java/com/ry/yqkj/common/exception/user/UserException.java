package com.ry.yqkj.common.exception.user;

import com.ry.yqkj.common.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author ry.yqkj
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
