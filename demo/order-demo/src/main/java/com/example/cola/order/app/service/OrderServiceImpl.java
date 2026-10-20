package com.example.cola.order.app.service;

import com.alibaba.cola.dto.Command;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.framework.DefaultExceptionHandler;
import com.example.cola.order.app.executor.OrderOperateCmdExe;
import com.example.cola.order.app.executor.PlaceOrderCmdExe;
import com.example.cola.order.app.executor.query.GetOrderQryExe;
import com.example.cola.order.client.api.OrderServiceI;
import com.example.cola.order.client.dto.GetOrderQry;
import com.example.cola.order.client.dto.OrderCO;
import com.example.cola.order.client.dto.OrderOperateCmd;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/**
 * 服务实现：COLA 规范里这一层故意写得很薄——只做「一个接口方法转发给一个 Executor」。
 *
 * 说明：官方 Archetype 里异常会直接向上抛，没有统一拦截。本 Demo 在服务层显式接上
 * COLA 自带的 DefaultExceptionHandler，把 BizException 等收敛成失败 Response（含错误码），
 * 这样"抛业务异常、统一转错误码"的规范才真实可见。
 */
public class OrderServiceImpl implements OrderServiceI {

    private final PlaceOrderCmdExe placeOrderCmdExe;
    private final GetOrderQryExe getOrderQryExe;
    private final OrderOperateCmdExe orderOperateCmdExe;

    public OrderServiceImpl(PlaceOrderCmdExe placeOrderCmdExe,
                            GetOrderQryExe getOrderQryExe,
                            OrderOperateCmdExe orderOperateCmdExe) {
        this.placeOrderCmdExe = placeOrderCmdExe;
        this.getOrderQryExe = getOrderQryExe;
        this.orderOperateCmdExe = orderOperateCmdExe;
    }

    @Override
    public SingleResponse<OrderCO> placeOrder(PlaceOrderCmd cmd) {
        try {
            return placeOrderCmdExe.execute(cmd);
        } catch (Exception e) {
            return toFailure(cmd, e);
        }
    }

    @Override
    public SingleResponse<OrderCO> getOrder(GetOrderQry qry) {
        try {
            return getOrderQryExe.execute(qry);
        } catch (Exception e) {
            return toFailure(qry, e);
        }
    }

    @Override
    public SingleResponse<OrderCO> operateOrder(OrderOperateCmd cmd) {
        try {
            return orderOperateCmdExe.execute(cmd);
        } catch (Exception e) {
            return toFailure(cmd, e);
        }
    }

    private SingleResponse<OrderCO> toFailure(Command cmd, Exception e) {
        SingleResponse<OrderCO> response = new SingleResponse<OrderCO>();
        DefaultExceptionHandler.singleton.handleException(cmd, response, e);
        return response;
    }
}
