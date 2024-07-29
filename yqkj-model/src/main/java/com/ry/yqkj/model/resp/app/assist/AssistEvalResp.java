package com.ry.yqkj.model.resp.app.assist;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author : lihy
 * @Description : 助教星级以及标签信息
 * @date : 2024/5/19 11:14 下午
 */
@Data
public class AssistEvalResp implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 助教标签（默认获取三个）
     */
    private List<String> tags;
    /**
     * 分数
     */
    private BigDecimal score;
}
