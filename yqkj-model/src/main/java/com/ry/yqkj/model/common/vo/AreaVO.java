package com.ry.yqkj.model.common.vo;

import lombok.Data;

import java.io.Serializable;

/*
 * since: 2024/5/15 16:06
 * author: lihy
 * description: 地区
 */
@Data
public class AreaVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 0 = 省份、直辖市  1 = 市、 2= 县、区
     */
    private Integer level;
    /**
     * 父层级
     */
    private String parentCode;

    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
}
