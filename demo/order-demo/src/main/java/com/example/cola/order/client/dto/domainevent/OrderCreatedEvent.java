package com.example.cola.order.client.dto.domainevent;

import com.alibaba.cola.event.DomainEventI;

/** 领域事件：下单成功。命名遵循「实体 + 动词过去式 + Event」的 COLA 规范。 */
public class OrderCreatedEvent implements DomainEventI {

    private final String orderId;
    private final String buyerId;
    private final long payAmountCent;

    public OrderCreatedEvent(String orderId, String buyerId, long payAmountCent) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.payAmountCent = payAmountCent;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public long getPayAmountCent() {
        return payAmountCent;
    }
}
