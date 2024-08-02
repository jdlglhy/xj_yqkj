package com.ry.yqkj.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.yqkj.model.resp.app.assist.AssistEvalResp;
import com.ry.yqkj.system.domain.OrderEval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author : lihy
 * @Description : 评价 mapper
 * @date : 2024/5/19 11:32 下午
 */
@Mapper
public interface OrderEvalMapper extends BaseMapper<OrderEval> {


    /**
     * 助教评价信息
     *
     * @param assistSet
     * @return
     */
    List<AssistEvalResp> selectAssistEval(@Param("assistIdSet") Set<Long> assistSet);

}
