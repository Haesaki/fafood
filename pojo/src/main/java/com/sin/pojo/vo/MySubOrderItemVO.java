package com.sin.pojo.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 用户中心
 * 我的订单列表嵌套商品
 */
@Data
@ToString
public class MySubOrderItemVO {
    private String itemId;
    private String itemImg;
    private String itemName;
    private String itemSpecName;
    private Integer buyCounts;
    private Integer price;
}
