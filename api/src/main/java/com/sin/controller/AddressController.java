package com.sin.controller;

import com.sin.pojo.UserAddress;
import com.sin.pojo.Users;
import com.sin.pojo.bo.AddressBO;
import com.sin.service.UserAddressService;
import com.sin.util.CookieUtils;
import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "地址相关的操作")
@RestController
@RequestMapping("address")
public class AddressController {
    @Autowired
    private UserAddressService addressService;

    /**
     * 用户在确认订单页面，可以对收获地址做一下操作
     * 1. 查询用户的收获地址列表
     * 2. 新增收获地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认的收货地址
     */
    @ApiOperation(value = "通过UserId查询收货地址列表")
    @PostMapping("/list")
    public HttpJSONResult getAddressList(@RequestParam String userId, HttpServletRequest request) {
        // 判断当前请求的用户和请求的id是不是一样的，如果不是一样的 直接放回NULL
//        Users user = JsonUtils.jsonToPojo(CookieUtils.getCookieValue(request, "user", true), Users.class);
        if (StringUtils.isBlank(userId))
            return HttpJSONResult.errorMsg("ERROR WITH USERID");

        List<UserAddress> list = addressService.queryAll(userId);

        return HttpJSONResult.ok(list);
    }

    @ApiOperation(value = "添加新的地址")
    @PostMapping("/add")
    public HttpJSONResult add(@RequestBody AddressBO addressBO, HttpServletRequest request) {

        // 判断当前请求的用户和请求的id是不是一样的，如果不是一样的 直接放回NULL
        Users user = JsonUtils.jsonToPojo(CookieUtils.getCookieValue(request, "user", true), Users.class);
        if (addressBO == null || user == null || !addressBO.getUserId().equals(user.getId()))
            return HttpJSONResult.errorMsg("ERROR WITH USERID");

        HttpJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.addNewUserAddress(addressBO);

        return HttpJSONResult.ok();
    }

    private HttpJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return HttpJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return HttpJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return HttpJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return HttpJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return HttpJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return HttpJSONResult.errorMsg("收货地址信息不能为空");
        }

        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
    @PostMapping("/update")
    public HttpJSONResult update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return HttpJSONResult.errorMsg("修改地址错误：addressId不能为空");
        }

        HttpJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.updateUserAddress(addressBO);

        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址", httpMethod = "POST")
    @PostMapping("/delete")
    public HttpJSONResult delete(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return HttpJSONResult.errorMsg("");
        }

        addressService.deleteUserAddress(userId, addressId);
        return HttpJSONResult.ok();
    }

    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址", httpMethod = "POST")
    @PostMapping("/setDefalut")
    public HttpJSONResult setDefalut(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return HttpJSONResult.errorMsg("");
        }

        addressService.updateUserAddressToBeDefault(userId, addressId);
        return HttpJSONResult.ok();
    }
}
