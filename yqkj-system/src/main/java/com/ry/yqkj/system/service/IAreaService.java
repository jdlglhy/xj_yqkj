package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.model.common.vo.AreaVO;
import com.ry.yqkj.system.domain.Area;

import java.util.List;

/**
 * @author : lihy
 * @Description : 区域管理 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IAreaService extends IService<Area> {


    /**
     * 通过区域名称和层级获取数据
     *
     * @param areaName
     * @param level
     */
    List<AreaVO> getChildren(String areaName, Integer level);

}
