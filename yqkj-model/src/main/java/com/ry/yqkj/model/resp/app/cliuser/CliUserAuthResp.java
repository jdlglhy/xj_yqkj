package com.ry.yqkj.model.resp.app.cliuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : lihy
 * @Description : 获取当前用户身份信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class CliUserAuthResp implements Serializable {


    /**
     * 是否是助教
     */
    private boolean isAssist;
    /**
     * 是否是区域代理
     */
    private boolean isAreaAgent;
    /**
     * 是否是球馆
     */
    private boolean isBilliardHall;
}
