package top.fuyuaaa.shadowpuppets.service;

import top.fuyuaaa.shadowpuppets.model.PageVO;
import top.fuyuaaa.shadowpuppets.model.bo.GoodsOrderBO;
import top.fuyuaaa.shadowpuppets.model.qo.GoodsOrderQO;
import top.fuyuaaa.shadowpuppets.model.vo.GoodsOrderVO;

import java.util.List;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-14 23:32
 */
public interface GoodsOrderService {

    GoodsOrderBO addNewGoodsOrder(GoodsOrderBO goodsOrderBO);

    GoodsOrderBO getById(String orderId);

    GoodsOrderVO getOrderVOById(String orderId);

    List<GoodsOrderBO> getOrderList(GoodsOrderQO goodsOrderQO);

    Integer count(GoodsOrderQO goodsOrderQO);

    PageVO<GoodsOrderVO> getOrderVOList(GoodsOrderQO goodsOrderQO);

    Boolean cancelGoodsOrderById(String orderId);

    Boolean confirmReceipt(String orderId);

    String getAliPayUrl(String orderId);

    Boolean checkOrderPaidAndUpdateOrderStatus(String orderId);

    void ship(String orderId, Double expressFee);
}
