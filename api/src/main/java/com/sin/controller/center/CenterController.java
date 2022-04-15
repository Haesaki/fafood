package com.sin.controller.center;

import com.sin.pojo.Users;
import com.sin.service.center.CenterUserService;
import com.sin.util.HttpJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
        if (user.getFace() == null || StringUtils.isBlank(user.getFace()))
            user.setFace("https://img1.baidu.com/it/u=4126345688,2740482511&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400");
        return HttpJSONResult.ok(user);
    }

}
