package com.sin.controller;

import com.sin.pojo.bo.UserBO;
import com.sin.service.UserService;
import com.sin.util.HttpJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return HttpJSONResult.ok();
    }
}
