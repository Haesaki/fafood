package com.sin.controller.center;

import com.sin.controller.BaseController;
import com.sin.pojo.vo.OrderStatusCountsVO;
import com.sin.util.HttpJSONResult;
import com.sin.util.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(value = "orders operations in the user center")
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {
    // get the order Status
    @ApiOperation(value = "get the order status")
    @PostMapping("/statusCounts")
    public HttpJSONResult statusCounts(@ApiParam(name = "userId", value = "userId", required = true)
                                       @RequestParam String userId){
        if(StringUtils.isBlank(userId))
            return HttpJSONResult.errorMsg("BLANK USER ID");
        OrderStatusCountsVO orderStatusCountsVO = myOrdersService.getOrderStatusCounts(userId);
        return HttpJSONResult.ok(orderStatusCountsVO);
    }

    @ApiOperation(value = "query order list", notes = "query order list", httpMethod = "POST")
    @PostMapping("/query")
    public HttpJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(required = true) String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam(required = false) Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam(required = false) Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam(required = false) Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return HttpJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.queryMyOrders(userId,
                orderStatus,
                page,
                pageSize);

        return HttpJSONResult.ok(grid);
    }

    // seller do not have the privileges to operate the back end.
    @ApiOperation(value="商家发货", notes="商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public HttpJSONResult deliver(
            @ApiParam(name = "orderId", value = "orderId", required = true)
            @RequestParam String orderId) throws Exception {

        if (StringUtils.isBlank(orderId)) {
            return HttpJSONResult.errorMsg("orderId is blank");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return HttpJSONResult.ok();
    }

    @ApiOperation(value="用户确认收货", notes="用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public HttpJSONResult confirmReceive(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        HttpJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return HttpJSONResult.errorMsg("订单确认收货失败！");
        }

        return HttpJSONResult.ok();
    }

    @ApiOperation(value="用户删除订单", notes="用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public HttpJSONResult delete(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        HttpJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if (!res) {
            return HttpJSONResult.errorMsg("订单删除失败！");
        }

        return HttpJSONResult.ok();
    }


    @ApiOperation(value = "查询订单动向", notes = "查询订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public HttpJSONResult trend(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return HttpJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.getOrdersTrend(userId,
                page,
                pageSize);

        return HttpJSONResult.ok(grid);
    }
}
