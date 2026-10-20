package com.example.cola.order.app.executor.query;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.BizException;
import com.example.cola.order.app.convertor.OrderConvertor;
import com.example.cola.order.client.dto.ErrorCode;
import com.example.cola.order.client.dto.GetOrderQry;
import com.example.cola.order.client.dto.OrderCO;
import com.example.cola.order.domain.gateway.OrderGateway;
import com.example.cola.order.domain.order.Order;

/** 查询订单执行器（CQRS 读端，与写 Executor 分开放置）。 */
public class GetOrderQryExe {

    private final OrderGateway orderGateway;

    public GetOrderQryExe(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public SingleResponse<OrderCO> execute(GetOrderQry qry) {
        Order order = orderGateway.get(qry.getOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.B_ORDER_NOT_FOUND,
                    ErrorCode.B_ORDER_NOT_FOUND.getErrDesc() + ": " + qry.getOrderId());
        }
        return SingleResponse.of(OrderConvertor.toCO(order));
    }
}
