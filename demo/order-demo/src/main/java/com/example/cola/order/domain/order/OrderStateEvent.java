package com.example.cola.order.domain.order;

/** 订单状态机事件（状态机的 E） */
public enum OrderStateEvent {
    PAY,
    SHIP,
    CONFIRM,
    CANCEL,
    UPDATE_REMARK
}
