package com.example.cola.order.domain.gateway;

import com.example.cola.order.domain.order.Order;

/**
 * 订单网关：Domain 层只定义接口，具体存储（本 Demo 用内存 Map，真实工程用 MyBatis/DO）
 * 落在 Infrastructure 层实现——这就是 DIP（依赖倒置）让领域层不依赖任何技术中间件。
 */
public interface OrderGateway {

    void save(Order order);

    Order get(String orderId);
}
