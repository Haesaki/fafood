package com.sin.controller;

import com.sin.pojo.OrderStatus;
import com.sin.pojo.bo.ShopcartBO;
import com.sin.pojo.bo.SubmitOrderBO;
import com.sin.pojo.vo.MerchantOrdersVO;
import com.sin.pojo.vo.OrderVO;
import com.sin.service.OrderService;
import com.sin.subenum.OrderStatusEnum;
import com.sin.subenum.PayMethod;
import com.sin.util.CookieUtils;
import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "orders related")
@RestController
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private final String payReturnUrl = "http://localhost:8080/fafood/payment/createMerchantOrder";
    private final String FAFOOD_SHOPCART = "FAFOOD_SHOPCART";
    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RedisTemplate redisTemplate;

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

        String shopcartJson = redisOperator.get(FAFOOD_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return HttpJSONResult.errorMsg("ERROR IN SHOPPING CART");
        }

        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        // create order
        OrderVO orderVO = orderService.createOrder(shopcartList, submitOrderBO);
        String orderId = orderVO.getOrderId();

        // remove ordered items in the shopping cart
        shopcartList.removeAll(orderVO.getToBeRemovedShopcatdList());
        redisOperator.set(FAFOOD_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        // record this to the cookie
        CookieUtils.setCookie(request, response, FAFOOD_SHOPCART, JsonUtils.objectToJson(shopcartList));
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




















