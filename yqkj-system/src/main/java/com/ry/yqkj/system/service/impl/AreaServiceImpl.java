package com.ry.yqkj.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.common.vo.AreaVO;
import com.ry.yqkj.system.domain.Area;
import com.ry.yqkj.system.mapper.AreaMapper;
import com.ry.yqkj.system.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : lihy
 * @Description : 区域 服务层
 * @date : 2024/5/19 11:14 下午
 */
@Service
@Slf4j
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {


    @Override
    public List<AreaVO> getChildren(String areaName, Integer level) {
        LambdaQueryWrapper<Area> wrapper = new LambdaQueryWrapper<Area>();
        wrapper.eq(Area::getName, areaName);
        wrapper.eq(Area::getLevel, level);
        Area area = this.baseMapper.selectOne(wrapper);
        if (area == null) {
            return Lists.newArrayList();
        }
        String code = area.getCode();
        LambdaQueryWrapper<Area> childWrapper = new LambdaQueryWrapper<Area>();
        childWrapper.eq(Area::getParentCode, code);
        List<Area> areaList = this.baseMapper.selectList(childWrapper);
        if (CollectionUtil.isEmpty(areaList)) {
            return Lists.newArrayList();
        }
        return DozerUtil.mapList(areaList, AreaVO.class);
    }
}
