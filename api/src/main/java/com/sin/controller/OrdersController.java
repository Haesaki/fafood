package com.sin.controller;

import com.sin.pojo.OrderStatus;
import com.sin.pojo.bo.SubmitOrderBO;
import com.sin.pojo.vo.MerchantOrdersVO;
import com.sin.pojo.vo.OrderVO;
import com.sin.service.OrderService;
import com.sin.subenum.OrderStatusEnum;
import com.sin.subenum.PayMethod;
import com.sin.util.CookieUtils;
import com.sin.util.HttpJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "orders related")
@RestController
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private final String payReturnUrl = "http://localhost:8080/fafood/payment/createMerchantOrder";

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public HttpJSONResult create(
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)
                && !submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.type)) {
            return HttpJSONResult.errorMsg("支付方式不支持！");
        }

//        System.out.println(submitOrderBO.toString());

        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        /**
         * 1001
         * 2002 -> 用户购买
         * 3003 -> 用户购买
         * 4004
         */
        // TODO 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
//        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);
        String userString = CookieUtils.getCookieValue(request, "user", true);
        CookieUtils.setCookie(request, response, "user", userString, true);

        // 3. 用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);
        return HttpJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public HttpJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return HttpJSONResult.ok(orderStatus);
    }
}




















