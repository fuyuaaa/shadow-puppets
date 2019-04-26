package top.fuyuaaa.shadowpuppets.common.enums;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-15 10:08
 */
@SuppressWarnings("all")
public enum OrderStatusEnum {

    UN_PAID(0, "未支付"),
    PAID(1,"已支付"),
    WAIT_COMMENT(2,"待评价"),
    FINISHED(3,"已完成"),
    CLOSED(4,"已关闭");


    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() {
        return code;
    }

    public String desc() {
        return desc;
    }

    public static OrderStatusEnum find(int code) {
        for (OrderStatusEnum filterMode : OrderStatusEnum.values()) {
            if (filterMode.code == code) {
                return filterMode;
            }
        }
        return null;
    }
}
