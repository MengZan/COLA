package com.example.cola.order.domain.order;

/** 订单状态（状态机的 S） */
public enum OrderState {

    WAIT_PAY("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    REFUNDED("已退款");

    private final String desc;

    OrderState(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
