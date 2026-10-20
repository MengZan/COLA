package com.example.cola.order.app.eventhandler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.domainevent.OrderCreatedEvent;

/**
 * 领域事件处理器：@EventHandler 标记后由 EventRegister 注册到 EventHub，
 * 下单成功后 EventBus.fire(...) 同步触发到这里（演示：发短信通知）。
 */
@EventHandler
public class OrderCreatedHandler implements EventHandlerI<Response, OrderCreatedEvent> {

    private final Logger logger = LoggerFactory.getLogger(OrderCreatedHandler.class);

    @Override
    public Response execute(OrderCreatedEvent event) {
        logger.info("[领域事件] OrderCreatedEvent 已处理：通知买家 " + event.getBuyerId()
                + " 下单成功（订单 " + event.getOrderId() + "，实付 " + event.getPayAmountCent() + " 分）");
        return Response.buildSuccess();
    }
}
