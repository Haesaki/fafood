package com.sin.controller;

import com.sin.pojo.Users;
import com.sin.pojo.bo.ShopcartBO;
import com.sin.pojo.bo.UserBO;
import com.sin.pojo.vo.UsersVO;
import com.sin.service.UserService;
import com.sin.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Api(value = "注册登录接口")
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseController{
    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/usernameIsExist")
    public HttpJSONResult usernameIsExist(@RequestParam String username) {
        // 1. is blank?
        if (StringUtils.isBlank(username)) {
            return HttpJSONResult.errorMsg("Username is empty!");
        }

        // query for username
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) return HttpJSONResult.errorMsg("Username exists");
        // username is unique
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "post")
    @PostMapping("/regist")
    public HttpJSONResult regist(@RequestBody UserBO userBO,
                                 HttpServletRequest request, HttpServletResponse response) {
        // 0. UserBO 是有效的
        //    指的是 全部字段不为空，字符小于50，两次密码相同
        if (!userBO.isValidUserBO()) return HttpJSONResult.errorMsg("NOT VALID USER");
        String username = userBO.getUsername();
        // 1. 查询用户名是否是存在的
        if (userService.queryUsernameIsExist(username)) return HttpJSONResult.errorMsg("Username is EXIST");
        // 2. 实现注册
        Users user = null;
        try {
            user = userService.createUser(userBO);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpJSONResult.errorMsg("Internal Error in creating user!");
        }
        //  生成用户token 存入redis
        if (user != null) {
            UsersVO usersVO = conventUserVO(user);
            CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
            syncShopcartData(user.getId(), request, response);
        }
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "post")
    @PostMapping("/login")
    public HttpJSONResult loginRequest(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String passport = userBO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(passport))
            return HttpJSONResult.errorMsg("ERROR in USERNAME OR PASSPORT");
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(passport));
        if (userResult == null) return HttpJSONResult.errorMsg("username do not match the passport");
        // setNullProperty(userResult);
        //  生成用户token 存入redis
        UsersVO usersVO = conventUserVO(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        //  同步其他设备的数据
        syncShopcartData(userResult.getId(), request, response);
        return HttpJSONResult.ok(userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "post")
    @PostMapping("/logout")
    public HttpJSONResult logoutRequest(@RequestBody String userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CookieUtils.deleteCookie(request, response, "user");
        // 用户退出登录,需要清空购物车
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        // 分布式会话清楚其他数据
        CookieUtils.deleteCookie(request, response, FAFOOD_SHOPCART);
        return HttpJSONResult.ok();
    }

    /**
     * 注册或则是登录成功后，将cookie同步到redis中
     * 1. redis 存在数据
     * cookie购物车为空 redis中的数据直接放入cookie中
     * cookie购物车不为空 合并全部数据
     * cookie中某个商品在redis中存在 以cookie为主，删除redis里面的
     * 2. redis 中无数据
     * cookie中购物车数据为空 不处理
     * cookie中购物车存在数据 将cookie中购物车全部数据加入到redis
     */
    private void syncShopcartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        String shopcartJsonRedis = redisOperator.get(FAFOOD_SHOPCART + ":" + userId);
        String shopcartJsonCookie = CookieUtils.getCookieValue(request, FAFOOD_SHOPCART, true);
        if (StringUtils.isBlank(shopcartJsonRedis)) {
            if (!StringUtils.isBlank(shopcartJsonCookie)) {
                redisOperator.set(FAFOOD_SHOPCART + ":" + userId, shopcartJsonCookie);
            }
        } else {
            if (StringUtils.isBlank(shopcartJsonCookie)) {
                CookieUtils.setCookie(request, response, FAFOOD_SHOPCART, shopcartJsonRedis);
            } else {
                /**
                 * 存在的商品 优先cookie 把cookie中对应数量 覆盖redis
                 * 将该项商品标记为待删除，统一放入一个等待删除的list
                 * 如果按照常规思路是一个二重循环 效率有点低
                 * 可以用hash表记录一个数据，然后在遍历另外一个数据就行了
                 */
                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartJsonCookie, ShopcartBO.class);
                // Hash表 key为  specId val 为 shopcartBo
                Map<String, ShopcartBO> specIdMap = new HashMap<>();
                // 先遍历 redis中的数据
                for (ShopcartBO sc : shopcartListRedis) {
                    if (!specIdMap.containsKey(sc.getSpecId())) {
                        specIdMap.put(sc.getSpecId(), sc);
                    }
                }
                // 再遍历cookie中数据
                for (ShopcartBO sc : shopcartListCookie) {
                    if (!specIdMap.containsKey(sc.getSpecId())) {
                        specIdMap.put(sc.getSpecId(), sc);
                    } else {
                        specIdMap.get(sc.getSpecId()).setBuyCount(sc.getBuyCount());
                    }
                }
                List<ShopcartBO> unionList = (List<ShopcartBO>) specIdMap.values();
                String unionStr = JsonUtils.objectToJson(unionList);
                CookieUtils.setCookie(request, response, FAFOOD_SHOPCART, unionStr);
                redisOperator.set(FAFOOD_SHOPCART + ":" + userId, unionStr);
            }
        }
    }
}
