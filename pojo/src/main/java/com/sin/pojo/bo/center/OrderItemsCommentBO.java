package com.sin.pojo.bo.center;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderItemsCommentBO {
    private String commentId;
    private String itemId;
    private String itemName;
    private String itemSpecId;
    private String itemSpecName;
    private Integer commentLevel;
    private String content;
}
