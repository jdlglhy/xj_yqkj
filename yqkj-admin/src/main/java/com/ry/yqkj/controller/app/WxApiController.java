package com.ry.yqkj.controller.app;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.yqkj.common.core.controller.BaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.common.core.domain.model.CodeSessionModel;
import com.ry.yqkj.common.utils.DozerUtil;
import com.ry.yqkj.model.req.app.WxAuthorizeReq;
import com.ry.yqkj.model.resp.CallBackResponse;
import com.ry.yqkj.model.resp.CodeSessionResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferBalanceResp;
import com.ry.yqkj.model.resp.app.cashwd.TransferNotifyResp;
import com.ry.yqkj.system.component.MsgTemplateComponent;
import com.ry.yqkj.system.component.WxCommonComponent;
import com.ry.yqkj.system.component.WxPayComponent;
import com.ry.yqkj.system.domain.ServiceOrder;
import com.ry.yqkj.system.service.ICashWdService;
import com.ry.yqkj.system.service.IServiceOrderService;
import com.ry.yqkj.system.service.IWxUserService;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;

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

    @Resource
    private ICashWdService cashWdService;

    @Resource
    private WxCommonComponent wxCommonComponent;

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
        Transaction transaction = wxPayComponent.notifyPayParser(request);
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

    @ApiOperation("用户确认收款回调")
    @PostMapping("/wechat/notify_user_confirm")
    public void userConfirmNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //解析转账回调参数
        TransferNotifyResp transferNotifyResp = wxPayComponent.notifyTransferParser(request);
        logger.info("用户确认收款回调:{}", transferNotifyResp);
        //转账成功
        if (ObjectUtil.equal(transferNotifyResp.getState(), "SUCCESS")) {
            cashWdService.notifySuccess(transferNotifyResp);
            CallBackResponse callBackResponse = new CallBackResponse("SUCCESS", "OK");
            // 3. 将应答转换为 JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(callBackResponse);
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
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
//
    @PostMapping("/transfer")
    @ApiOperation("转账测试")
    public R<Void> testTransfer() throws Exception {
        TransferBalanceResp transferBalanceResp = wxPayComponent.transferToBalance("otQEg7eeTizl9qZG3NMgnrEh5XhI", NonceUtil.createNonce(16), new BigDecimal("0.1"), "转账test");
        logger.info("resp={}", transferBalanceResp);
        return R.ok();
    }

    @GetMapping("/url_link")
    @ApiOperation("生成小程序urlLink")
    public R<String> generateUrlLink() {
        return R.ok(wxCommonComponent.generateUrlLink());
    }



    @GetMapping("/send_msg")
    @ApiOperation("发送短息")
    public R<Void> sendMsg() {
        ServiceOrder serviceOrder = serviceOrderService.getById(1899056138505207810L);
        msgTemplateComponent.sendNewOrderSmsMsg(serviceOrder);
        return R.ok();
    }
}
