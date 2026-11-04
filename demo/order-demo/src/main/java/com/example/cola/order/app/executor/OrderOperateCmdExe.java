package com.example.cola.order.app.executor;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.statemachine.StateMachine;
import com.example.cola.order.app.convertor.OrderConvertor;
import com.example.cola.order.client.dto.ErrorCode;
import com.example.cola.order.client.dto.OperateType;
import com.example.cola.order.client.dto.OrderCO;
import com.example.cola.order.client.dto.OrderOperateCmd;
import com.example.cola.order.domain.gateway.OrderGateway;
import com.example.cola.order.domain.order.Order;
import com.example.cola.order.domain.order.OrderState;
import com.example.cola.order.domain.order.OrderStateEvent;

/** 订单操作执行器：把对外 OperateType 翻译成领域状态机事件，真正的合法性守卫在实体 + 状态机里。 */
public class OrderOperateCmdExe {

    private final OrderGateway orderGateway;
    private final StateMachine<OrderState, OrderStateEvent, Order> stateMachine;

    public OrderOperateCmdExe(OrderGateway orderGateway,
                              StateMachine<OrderState, OrderStateEvent, Order> stateMachine) {
        this.orderGateway = orderGateway;
        this.stateMachine = stateMachine;
    }

    public SingleResponse<OrderCO> execute(OrderOperateCmd cmd) {
        Order order = orderGateway.get(cmd.getOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.B_ORDER_NOT_FOUND,
                    ErrorCode.B_ORDER_NOT_FOUND.getErrDesc() + ": " + cmd.getOrderId());
        }
        order.operate(stateMachine, toEvent(cmd.getOperateType()));
        orderGateway.save(order);
        return SingleResponse.of(OrderConvertor.toCO(order));
    }

    private OrderStateEvent toEvent(OperateType type) {
        switch (type) {
            case PAY:
                return OrderStateEvent.PAY;
            case SHIP:
                return OrderStateEvent.SHIP;
            case CONFIRM_RECEIPT:
                return OrderStateEvent.CONFIRM;
            case CANCEL:
                return OrderStateEvent.CANCEL;
            case UPDATE_REMARK:
                return OrderStateEvent.UPDATE_REMARK;
            default:
                throw new IllegalArgumentException("未知操作类型: " + type);
        }
    }
}
