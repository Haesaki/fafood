package com.sin.controller;

import com.sin.pojo.bo.ShopcartBO;
import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController extends BaseController{
    private RedisOperator redisOperator;

    @Autowired
    public ShopcatController(RedisOperator redisOperator){
        this.redisOperator = redisOperator;
    }
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public HttpJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId)) {
            return HttpJSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // 判断购物车中是否存在商品，如果有的话，counts累加
        String shopcartString = redisOperator.get(FAFOOD_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartBOList = null;
        if(StringUtils.isBlank(shopcartString)){
            shopcartBOList = new ArrayList<>();
            shopcartBOList.add(shopcartBO);
        } else{
            shopcartBOList = JsonUtils.jsonToList(shopcartString, ShopcartBO.class);
            // 判断购物车中是否存在该商品
            boolean isHaving = false;
            for(ShopcartBO sc : shopcartBOList){
                String specId = sc.getSpecId();
                if(specId.equals(shopcartBO.getSpecId())){
                    sc.setBuyCount(sc.getBuyCount() + shopcartBO.getBuyCount());
                    isHaving = true;
                }
            }
            if(!isHaving){
                shopcartBOList.add(shopcartBO);
            }
        }
        redisOperator.set(FAFOOD_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBOList));
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public HttpJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return HttpJSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除后端购物车中的商品
        String shopcartJson = redisOperator.get(FAFOOD_SHOPCART + ":" + userId);
        if(StringUtils.isNotBlank(shopcartJson)){
            // redis 中已经存在了该用户的购物车
            List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            for(ShopcartBO sc : shopcartList){
                String sepcId = sc.getSpecId();
                if(sepcId.equals(itemSpecId)){
                    shopcartList.remove(sc);
                    break;
                }
            }
            redisOperator.set(FAFOOD_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        }
        return HttpJSONResult.ok();
    }

}
