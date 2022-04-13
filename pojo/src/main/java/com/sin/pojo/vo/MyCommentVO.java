package com.sin.pojo.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class MyCommentVO {
    private String commentId;
    private String content;
    private Date createdTime;
    private String itemId;
    private String itemName;
    private String specName;
    private String itemImg;
}
