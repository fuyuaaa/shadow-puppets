package top.fuyuaaa.shadowpuppets.model.bo;

import lombok.Data;
import top.fuyuaaa.shadowpuppets.common.enums.OrderStatusEnum;
import top.fuyuaaa.shadowpuppets.model.BaseModel;

import java.util.List;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-14 23:20
 */
@Data
public class GoodsOrderBO extends BaseModel {
    private static final long serialVersionUID = -6775800978873507724L;
    private String id;
    private Integer userId;
    private List<Integer> shoppingCartIdList;
    private List<GoodsOrderSimpleBO> goodsOrderSimpleBOList;
    /**
     * 快递费
     */
    private Double expressFee;
    /**
     * 成交价
     */
    private Double dealPrice;
    /**
     * 订单状态（0未付款/1已付款）
     */
    private OrderStatusEnum status;
}
