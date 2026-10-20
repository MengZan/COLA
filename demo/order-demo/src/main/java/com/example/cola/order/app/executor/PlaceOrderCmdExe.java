package com.example.cola.order.app.executor;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.event.EventBusI;
import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.app.convertor.OrderConvertor;
import com.example.cola.order.app.extension.PlaceOrderValidatorExtPt;
import com.example.cola.order.app.extension.PriceCalcExtPt;
import com.example.cola.order.app.extension.PriceResult;
import com.example.cola.order.client.dto.OrderCO;
import com.example.cola.order.client.dto.PlaceOrderCmd;
import com.example.cola.order.client.dto.domainevent.OrderCreatedEvent;
import com.example.cola.order.domain.gateway.OrderGateway;
import com.example.cola.order.domain.order.Order;

/**
 * 下单命令执行器（用例编排层）。标准四步：
 * 校验（扩展点） -> 计价（扩展点） -> 领域实体落库（Gateway） -> 发布领域事件（EventBus）。
 */
public class PlaceOrderCmdExe {

    private final Logger logger = LoggerFactory.getLogger(PlaceOrderCmdExe.class);

    private final ExtensionExecutor extensionExecutor;
    private final OrderGateway orderGateway;
    private final EventBusI eventBus;

    public PlaceOrderCmdExe(ExtensionExecutor extensionExecutor, OrderGateway orderGateway, EventBusI eventBus) {
        this.extensionExecutor = extensionExecutor;
        this.orderGateway = orderGateway;
        this.eventBus = eventBus;
    }

    public SingleResponse<OrderCO> execute(PlaceOrderCmd cmd) {
        logger.info("[CmdExe] 开始下单，场景=" + cmd.getBizScenario().getUniqueIdentity());

        // 【注意】COLA 的 BizScenario.getIdentityWithDefault*() 有副作用：兜底查找会就地把
        // scenario/useCase 改写成默认值。一个 Cmd 内若连续调两次扩展点，第二次会拿到被降级的坐标。
        // 所以先捕获原始坐标，每次扩展调用都克隆一份新的 BizScenario。
        String[] parts = cmd.getBizScenario().getUniqueIdentity().split("\\.");

        // 1) 校验（走扩展点）
        extensionExecutor.executeVoid(PlaceOrderValidatorExtPt.class, fresh(parts),
                ext -> ext.validate(cmd));

        // 2) 计价（走扩展点）
        PriceResult price = extensionExecutor.execute(PriceCalcExtPt.class, fresh(parts),
                ext -> ext.calc(cmd));

        // 3) 领域实体 + Gateway 落库
        Order order = new Order(cmd.getBuyerId(), cmd.getItemName(), cmd.getQuantity(),
                cmd.totalAmountCent(), price.getPayAmountCent(), price.getBenefitLabel());
        orderGateway.save(order);

        // 4) 发布领域事件
        eventBus.fire(new OrderCreatedEvent(order.getOrderId(), order.getBuyerId(), order.getPayAmountCent()));

        logger.info("[CmdExe] 下单完成，订单号=" + order.getOrderId());
        return SingleResponse.of(OrderConvertor.toCO(order));
    }

    private BizScenario fresh(String[] parts) {
        return BizScenario.valueOf(parts[0], parts[1], parts[2]);
    }
}
