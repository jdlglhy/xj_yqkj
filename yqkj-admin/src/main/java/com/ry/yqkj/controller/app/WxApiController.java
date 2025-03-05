package com.ry.yqkj.controller.app;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.app.TransferRequest;
import com.ry.yqkj.model.req.app.WxAuthorizeReq;
import com.ry.yqkj.model.resp.CodeSessionResp;
import com.ry.yqkj.system.component.MsgTemplateComponent;
import com.ry.yqkj.system.component.WxPayComponent;
import com.ry.yqkj.system.service.IServiceOrderService;
import com.ry.yqkj.system.service.IWxUserService;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author : lihy
 * @Description : 微信授权管理
 * @date : 2024/5/18 12:11 上午
 */
@RestController
@RequestMapping("/wx_api")
@Api("微信API")
public class WxApiController extends BaseController {

    @Resource
    private IWxUserService wxUserService;

    @Resource
    private WxPayComponent wxPayComponent;

    @Resource
    private IServiceOrderService serviceOrderService;

    @PostMapping("/auth")
    @ApiOperation("微信授权方法（通过code登陆）")
    public R<CodeSessionResp> bind(@Validated @RequestBody WxAuthorizeReq req) {
        CodeSessionModel codeSession = wxUserService.bindWxUser(req.getCode());
        return R.ok(DozerUtil.map(codeSession, CodeSessionResp.class));
    }

    @ApiOperation("支付成功回调")
    @PostMapping("/wechat/notify_pay")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //解析支付回调参数
        Transaction transaction = wxPayComponent.notifyParser(request);
        Transaction.TradeStateEnum state = transaction.getTradeState();
        String orderNo = transaction.getOutTradeNo();
        logger.info("orderNo={},state={}", orderNo, state);
        if (ObjectUtil.equal(state, Transaction.TradeStateEnum.SUCCESS)) {
            //根据自己的需求处理相应的业务逻辑,异步
            serviceOrderService.payNotify(orderNo, transaction.getTransactionId());
            //通知微信回调成功
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
        } else {
            logger.error("微信回调失败,JsapiPayController.payNotify.transaction：{}", transaction.toString());
            //通知微信回调失败
            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
        }
    }

    @Resource
    private MsgTemplateComponent msgTemplateComponent;

//    @PostMapping("/send")
//    @ApiOperation("测试消息发送")
//    public R<Void> send() {
//        msgTemplateComponent.sendPayOrderMsgTest();
//        return R.ok();
//    }

    @PostMapping("/transfer")
    @ApiOperation("转账测试")
    public R<Void> testTransfer() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setOutBatchNo("20250301221122");
        request.setBatchName("佣金收入");
        request.setBatchRemark("佣金收入2");
        request.setTotalNum(1);
        // 单位：分
        request.setTotalAmount(10);
        List<TransferRequest.TransferDetail> detailList = Lists.newArrayList();
        TransferRequest.TransferDetail detail = new TransferRequest.TransferDetail();
        detail.setOutDetailNo("20250301221122-1");
        detail.setTransferAmount(10);
        detail.setOpenid("otQEg7eeTizl9qZG3NMgnrEh5XhI");
        detail.setTransferRemark("佣金收入3");
        detailList.add(detail);
        request.setTransferDetailList(detailList);
        String data = wxPayComponent.transferToBalance("otQEg7eeTizl9qZG3NMgnrEh5XhI",10,"转账test");
        logger.info("resp={}",data);
        return R.ok();
    }
}
