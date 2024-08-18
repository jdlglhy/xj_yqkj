package com.ry.yqkj.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 客户端用户对应的用户身份信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
@TableName(value = "cli_user_auth")
public class CliUserAuth implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端用户ID
     */
    private Long cliUserId;
    /**
     * 用户类型
     */
    private String userType;

    /**
     * 目标用户ID
     */
    private Long targetId;
}
