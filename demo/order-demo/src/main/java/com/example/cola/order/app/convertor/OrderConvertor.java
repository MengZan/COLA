package com.example.cola.order.app.convertor;

import com.example.cola.order.client.dto.OrderCO;
import com.example.cola.order.domain.order.Order;

/** 实体 -> CO 转换器：层间对象不直接穿透。 */
public class OrderConvertor {

    private OrderConvertor() {
    }

    public static OrderCO toCO(Order order) {
        OrderCO co = new OrderCO();
        co.setOrderId(order.getOrderId());
        co.setBuyerId(order.getBuyerId());
        co.setItemName(order.getItemName());
        co.setQuantity(order.getQuantity());
        co.setTotalAmountCent(order.getTotalAmountCent());
        co.setPayAmountCent(order.getPayAmountCent());
        co.setBenefitLabel(order.getBenefitLabel());
        co.setState(order.getState().name());
        co.setStateDesc(order.getState().getDesc());
        return co;
    }
}
