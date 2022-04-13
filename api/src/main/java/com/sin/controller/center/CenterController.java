package com.sin.controller.center;

import com.sin.pojo.Users;
import com.sin.service.center.CenterUserService;
import com.sin.util.HttpJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户中心 用户展示的相关接口")
@RestController
@RequestMapping("center")
public class CenterController {
    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息", httpMethod = "GET")
    @GetMapping("userInfo")
    public HttpJSONResult userInfo(@ApiParam(name = "userId", value = "用户id", required = true)
                                   @RequestParam String userId) {
        Users user = centerUserService.queryUserInfo(userId);
        return HttpJSONResult.ok(user);
    }

}
