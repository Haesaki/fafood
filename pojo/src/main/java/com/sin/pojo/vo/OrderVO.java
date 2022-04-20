package com.sin.pojo.vo;

import com.sin.pojo.bo.ShopcartBO;

import java.util.List;

public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;

    private List<ShopcartBO> toBeRemovedShopcatdList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public List<ShopcartBO> getToBeRemovedShopcatdList() {
        return toBeRemovedShopcatdList;
    }


    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
    public void setToBeRemovedShopcatdList(List<ShopcartBO> toBeRemovedShopcatdList) {
        this.toBeRemovedShopcatdList = toBeRemovedShopcatdList;
    }
}