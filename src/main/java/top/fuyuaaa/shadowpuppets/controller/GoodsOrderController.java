package top.fuyuaaa.shadowpuppets.controller;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.fuyuaaa.shadowpuppets.common.annotations.NeedLogin;
import top.fuyuaaa.shadowpuppets.common.annotations.ValidateOrderOwner;
import top.fuyuaaa.shadowpuppets.common.Result;
import top.fuyuaaa.shadowpuppets.common.holders.LoginUserHolder;
import top.fuyuaaa.shadowpuppets.model.PageVO;
import top.fuyuaaa.shadowpuppets.model.bo.GoodsOrderBO;
import top.fuyuaaa.shadowpuppets.model.qo.GoodsOrderQO;
import top.fuyuaaa.shadowpuppets.model.vo.GoodsOrderVO;
import top.fuyuaaa.shadowpuppets.service.GoodsOrderService;
import top.fuyuaaa.shadowpuppets.service.GoodsService;
import top.fuyuaaa.shadowpuppets.service.ShoppingCartService;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-14 23:19
 */
@RestController
@RequestMapping("/goods/order")
@Slf4j
public class GoodsOrderController {

    @Autowired
    GoodsOrderService goodsOrderService;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    GoodsService goodsService;

    @PostMapping("/add")
    @NeedLogin
    public Result<String> addGoodsOrder(@RequestBody GoodsOrderBO goodsOrderBO) {
        goodsOrderBO = goodsOrderService.addNewGoodsOrder(goodsOrderBO);
        return Result.success(goodsOrderBO.getId()).setMsg("创建订单成功");
    }

    @PostMapping("/pay/url")
    @NeedLogin
    @ValidateOrderOwner
    public Result<String> getAliPayUrl(@RequestParam String orderId) {
        String aliPayUrl = goodsOrderService.getAliPayUrl(orderId);
        return Result.success(aliPayUrl).setMsg("正在跳转到支付宝...");
    }

    /**
     * 当支付完成回调订单详情页地址时，在前端页面构造方法中会调这个接口
     * 1. 查看这个订单是否已经支付
     * 2. 已支付 => 更新订单状态
     *
     * @param orderId 订单编号
     * @return
     */
    @PostMapping("/pay/check")
    @NeedLogin
    @ValidateOrderOwner
    public Result<Boolean> checkOrderPaidAndUpdateOrderStatus(@RequestParam String orderId) {
        Boolean success = goodsOrderService.checkOrderPaidAndUpdateOrderStatus(orderId);
        return success?Result.success(true).setMsg("支付成功"):Result.fail("支付未完成");
    }

    @PostMapping("/one")
    @NeedLogin
    @ValidateOrderOwner
    public Result<GoodsOrderVO> getGoodsOrder(@RequestParam String orderId) {
        GoodsOrderVO goodsOrderVO = goodsOrderService.getOrderVOById(orderId);
        return Result.success(goodsOrderVO);
    }

    @GetMapping("/user/list")
    @NeedLogin
    public Result<PageVO<GoodsOrderVO>> getGoodsOrderListByUser(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                                @RequestParam(defaultValue = "-1") Integer orderStatus) {

        Integer userId = LoginUserHolder.instance().get().getId();
        GoodsOrderQO goodsOrderQO = new GoodsOrderQO();
        goodsOrderQO.setUserId(userId);
        goodsOrderQO.setPageNum(page);
        goodsOrderQO.setPageSize(pageSize);
        goodsOrderQO.setStatus(orderStatus);
        PageHelper.startPage(page, pageSize);
        PageVO<GoodsOrderVO> pageVO = goodsOrderService.getOrderVOList(goodsOrderQO);
        return Result.success(pageVO);
    }

    @PostMapping("/user/cancel")
    @NeedLogin
    @ValidateOrderOwner
    public Result<String> cancelGoodsOrderById(@RequestParam String orderId) {
        if (StringUtils.isEmpty(orderId) || !goodsOrderService.cancelGoodsOrderById(orderId)) {
            return Result.fail("取消订单失败！");
        }
        return Result.success("取消订单成功！", "取消订单成功！");
    }

    @PostMapping("/user/received")
    @NeedLogin
    @ValidateOrderOwner
    public Result<String> confirmReceipt(@RequestParam String orderId) {
        if (StringUtils.isEmpty(orderId) || !goodsOrderService.confirmReceipt(orderId)) {
            return Result.fail("确认收货失败！");
        }
        return Result.success("取确认收货成功！", "确认收货成功！");
    }

}
