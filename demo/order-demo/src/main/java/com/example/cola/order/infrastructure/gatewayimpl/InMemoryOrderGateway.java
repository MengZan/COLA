package com.example.cola.order.infrastructure.gatewayimpl;

import com.example.cola.order.domain.gateway.OrderGateway;
import com.example.cola.order.domain.order.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单网关的内存实现。真实工程里这一层是 MyBatis Mapper + DO 与实体互转；
 * 本 Demo 用内存 Map 代替数据库，Domain/App 层完全无感。
 */
public class InMemoryOrderGateway implements OrderGateway {

    private final Map<String, Order> store = new ConcurrentHashMap<String, Order>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public void save(Order order) {
        if (order.getOrderId() == null) {
            order.setOrderId(String.format("ORD%04d", sequence.incrementAndGet()));
        }
        store.put(order.getOrderId(), order);
    }

    @Override
    public Order get(String orderId) {
        return orderId == null ? null : store.get(orderId);
    }
}
