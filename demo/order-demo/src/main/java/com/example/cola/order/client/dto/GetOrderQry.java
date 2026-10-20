package com.example.cola.order.client.dto;

import com.alibaba.cola.dto.Query;

/** 查询订单（CQRS 里的 Query 端） */
public class GetOrderQry extends Query {

    private static final long serialVersionUID = 1L;

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
