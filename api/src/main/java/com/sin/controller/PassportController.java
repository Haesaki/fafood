package com.sin.controller;

import com.sin.pojo.Users;
import com.sin.pojo.bo.UserBO;
import com.sin.service.UserService;
import com.sin.util.CookieUtils;
import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录接口")
@RestController
@RequestMapping("/passport")
public class PassportController {
    @Autowired
    private UserService userService;

    @GetMapping("/usernameIsExist")
    public HttpJSONResult usernameIsExist(@RequestParam String username) {
        // 1. is blank?
        if (StringUtils.isBlank(username)) {
            return HttpJSONResult.errorMsg("Username is empty!");
        }

        // query for username
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist)
            return HttpJSONResult.errorMsg("Username exists");
        // username is unique
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "post")
    @PostMapping("/regist")
    public HttpJSONResult regist(@RequestBody UserBO userBO) {
        // 0. UserBO 是有效的
        //    指的是 全部字段不为空，字符小于50，两次密码相同
        if (!userBO.isValidUserBO())
            return HttpJSONResult.errorMsg("NOT VALID USER");
        String username = userBO.getUsername();
        // 1. 查询用户名是否是存在的
        if (userService.queryUsernameIsExist(username))
            return HttpJSONResult.errorMsg("Username is EXIST");
        // 2. 实现注册
        try{
            userService.createUser(userBO);
        } catch (Exception e){
            e.printStackTrace();
            return HttpJSONResult.errorMsg("Internal Error in creating user!");
        }
        // TODO 生成用户token 存入redis
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "post")
    @PostMapping("/login")
    public HttpJSONResult loginRequest(@RequestBody UserBO userBO,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String passport = userBO.getPassword();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(passport))
            return HttpJSONResult.errorMsg("ERROR in USERNAME OR PASSPORT");
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(passport));
        if(userResult == null)
            return HttpJSONResult.errorMsg("username do not match the passport");
        setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
        // TODO 生成用户token 存入redis
        // TODO 同步其他设备的数据
        return HttpJSONResult.ok(userResult);
    }

    private Users setNullProperty(Users user){
        if(user == null)
            return null;
        user.setPassword(null);
        user.setMobile(null);
        user.setEmail(null);
        user.setCreatedTime(null);
        user.setUpdatedTime(null);
        user.setBirthday(null);
        return user;
    }


    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "post")
    @PostMapping("/logout")
    public HttpJSONResult logoutRequest(@RequestBody String userId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        CookieUtils.deleteCookie(request, response, "user");
        // TODO 用户退出登录,需要清空购物车
        // TODO 分布式会话清楚其他数据
        return HttpJSONResult.ok();
    }
}
