package com.sin.controller;

import com.sin.pojo.Orders;
import com.sin.pojo.Users;
import com.sin.pojo.vo.UsersVO;
import com.sin.service.center.MyOrdersService;
import com.sin.util.HttpJSONResult;
import com.sin.util.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {

    @Autowired
    private RedisOperator redisOperator;

    public static final String FAFOOD_SHOPCART = "FAFOOD_SHOPCART";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public final String payReturnUrl = "http://localhost:8080/fafood/payment/createMerchantOrder";

    public static final Integer COMMON_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
            File.separator + "images" +
            File.separator + "foodie" +
            File.separator + "faces";

    @Autowired
    public MyOrdersService myOrdersService;
    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return
     */
    public HttpJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return HttpJSONResult.errorMsg("ORDER DOES NOT EXIST!");
        }
        return HttpJSONResult.ok(order);
    }

    public UsersVO conventUserVO(Users userResult){
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), uniqueToken);
        UsersVO usersVO = new UsersVO();

        // userResult = setNullProperty(userResult);
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }

    public Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
