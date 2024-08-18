package com.ry.yqkj.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry.yqkj.common.core.page.PageResDomain;
import com.ry.yqkj.model.req.app.billiardhall.HallApplyRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallFillRequest;
import com.ry.yqkj.model.req.web.billiardhall.WebHallPageReq;
import com.ry.yqkj.model.resp.web.billiardhall.WebBilliardHallInfoResp;
import com.ry.yqkj.system.domain.BilliardHall;

/**
 * @author : lihy
 * @Description : 球馆 服务层
 * @date : 2024/5/19 11:14 下午
 */
public interface IBilliardHallService extends IService<BilliardHall> {

    /**
     * 申请入驻
     *
     * @param request
     */
    void apply(HallApplyRequest request);

    /**
     * 球馆入驻审批
     *
     * @param id     申请单ID
     * @param status 状态
     * @param reason 审批不通过原因
     */
    void examine(Long id, String status, String reason);

    /**
     * 完善球馆信息
     *
     * @param request 变更参数
     */
    void fillBilliardHallInfo(WebHallFillRequest request);

    /**
     * 球馆分页数据
     *
     * @param req 分页请求参数
     * @return PageResDomain
     */
    PageResDomain<WebBilliardHallInfoResp> page(WebHallPageReq req);


    /**
     * 球馆审核分页数据
     *
     * @param req 分页请求参数
     * @return PageResDomain
     */
    PageResDomain<WebBilliardHallInfoResp> examinePage(WebHallPageReq req);


    /**
     * 获取最新的入驻审批信息
     *
     * @return BilliardHall
     */
    BilliardHall info();


    /**
     * 通过ID获取球馆信息
     *
     * @return BilliardHall
     */
    WebBilliardHallInfoResp detail(Long id);

}
