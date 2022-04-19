package com.sin.controller;

import com.sin.pojo.Carousel;
import com.sin.pojo.Category;
import com.sin.pojo.vo.CategoryVO;
import com.sin.pojo.vo.NewItemsVO;
import com.sin.service.CarouselService;
import com.sin.service.CategoryService;
import com.sin.subenum.YesOrNo;
import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Api(value = "首页")
@RestController
@RequestMapping("index")
public class IndexController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页联播图")
    @GetMapping("/carousel")
    public HttpJSONResult getCarousel(){
        List<Carousel> ret;
        String carouselStr = redisOperator.get("carousel");
        if(StringUtils.isBlank(carouselStr)){
            ret = carouselService.queryAllCarousels(YesOrNo.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(ret));
        } else{
            ret = JsonUtils.jsonToList(carouselStr, Carousel.class);
        }
        return HttpJSONResult.ok(ret);
    }

    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public HttpJSONResult cats() {
        List<Category> ret;
        String catsStr = redisOperator.get("catsFirst");
        if(StringUtils.isBlank(catsStr)){
            ret = categoryService.queryAllRootLevelCat();
            redisOperator.set("catsFirst", JsonUtils.objectToJson(ret));
        } else{
            ret = JsonUtils.jsonToList(catsStr, Category.class);
        }
        return HttpJSONResult.ok(ret);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public HttpJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return HttpJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> list;
        String subCatStr = redisOperator.get("subCat:" + String.valueOf(rootCatId));
        if(StringUtils.isBlank(subCatStr)){
            list = categoryService.getSubCatList(rootCatId);
            /**
             * 查询的key在redis中不存在，
             * 对应的id在数据库也不存在，
             * 此时被非法用户进行攻击，大量的请求会直接打在db上，
             * 造成宕机，从而影响整个系统，
             * 这种现象称之为缓存穿透。
             * 解决方案：把空的数据也缓存起来，比如空字符串，空对象，空数组或list
             */
            if(list != null && list.size() > 0){
                redisOperator.set("subCat:" + String.valueOf(rootCatId), JsonUtils.objectToJson(list));
            }else{
                redisOperator.set("subCat:" + String.valueOf(rootCatId), JsonUtils.objectToJson(list), 5*60);
            }
        } else{
            list = JsonUtils.jsonToList(subCatStr, CategoryVO.class);
        }
        return HttpJSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public HttpJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return HttpJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return HttpJSONResult.ok(list);
    }
}
