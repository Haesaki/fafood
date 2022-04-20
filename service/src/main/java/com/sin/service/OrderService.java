package com.sin.service;

import com.sin.pojo.OrderStatus;
import com.sin.pojo.bo.ShopcartBO;
import com.sin.pojo.bo.SubmitOrderBO;
import com.sin.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    // 创建订单的相关信息
    OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();
}
