package com.example.cola.order.domain.order;

import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;

/**
 * 订单状态机定义（使用 cola-statemachine 的 Fluent DSL）。这是领域规则，所以放在 Domain 层。
 *
 *   WAIT_PAY --PAY(实付金额>0)--> PAID --SHIP--> SHIPPED --CONFIRM--> COMPLETED
 *      |                          |
 *      +--CANCEL--> CANCELLED     +--CANCEL--> REFUNDED
 *      PAID --UPDATE_REMARK--> PAID（内部转换，状态不变）
 *
 * cola-statemachine 是无状态的：build 一次即可多线程共享，当前状态由实体持有、每次传入 fireEvent。
 * build() 会把机器按 machineId 注册到 StateMachineFactory，重复 build 同一 id 会抛错，所以整个应用只构建一次。
 */
public class OrderStateMachines {

    public static final String MACHINE_ID = "OrderStateMachine";

    private static final Logger logger = LoggerFactory.getLogger(OrderStateMachines.class);

    private OrderStateMachines() {
    }

    public static StateMachine<OrderState, OrderStateEvent, Order> create() {
        StateMachineBuilder<OrderState, OrderStateEvent, Order> builder = StateMachineBuilderFactory.create();

        builder.externalTransition()
                .from(OrderState.WAIT_PAY).to(OrderState.PAID).on(OrderStateEvent.PAY)
                .when(ctx -> ctx.getPayAmountCent() > 0)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to));

        builder.externalTransition()
                .from(OrderState.PAID).to(OrderState.SHIPPED).on(OrderStateEvent.SHIP)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to));

        builder.externalTransition()
                .from(OrderState.SHIPPED).to(OrderState.COMPLETED).on(OrderStateEvent.CONFIRM)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to));

        builder.externalTransition()
                .from(OrderState.WAIT_PAY).to(OrderState.CANCELLED).on(OrderStateEvent.CANCEL)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to + "（未支付，直接取消）"));

        builder.externalTransition()
                .from(OrderState.PAID).to(OrderState.REFUNDED).on(OrderStateEvent.CANCEL)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to + "（已支付，退款）"));

        builder.internalTransition()
                .within(OrderState.PAID).on(OrderStateEvent.UPDATE_REMARK)
                .perform((from, to, event, ctx) ->
                        logger.info("[状态机] 订单 " + ctx.getOrderId() + " " + from + " -[" + event + "]-> " + to + "（内部转换：只更新备注）"));

        return builder.build(MACHINE_ID);
    }
}
