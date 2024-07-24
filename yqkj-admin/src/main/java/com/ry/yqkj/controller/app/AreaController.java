package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.model.common.vo.AreaVO;
import com.ry.yqkj.system.service.IAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : lihy
 * @Description : 区域管理
 * @date : 2024/7/8 12:11 上午
 */
@RestController
@Api("区域管理")
public class AreaController extends WxBaseController {

    @Resource
    private IAreaService areaService;

    @GetMapping("/get/children")
    @ApiOperation("获取某个区域下的子集")
    public R<List<AreaVO>> getChildren(@ApiParam("区域名称") @RequestParam("areaName") String areaName,
                                       @ApiParam("层级") @RequestParam("level") Integer level) {
        return R.ok(areaService.getChildren(areaName, level));
    }


    @GetMapping("/get/province")
    @ApiOperation("获取省份")
    public R<List<AreaVO>> getProvince() {
        return R.ok(areaService.getProvinceList());
    }

}
