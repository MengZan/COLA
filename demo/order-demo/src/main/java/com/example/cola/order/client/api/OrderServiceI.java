package com.example.cola.order.client.api;

import com.alibaba.cola.dto.SingleResponse;
import com.example.cola.order.client.dto.*;

/**
 * 对外服务契约（对应脚手架 -client 模块的 api 包）。
 * Controller/RPC 调用方只认识这个接口 + DTO，碰不到领域实体。
 */
public interface OrderServiceI {

    /** 下单（写操作，走 PlaceOrderCmdExe） */
    SingleResponse<OrderCO> placeOrder(PlaceOrderCmd cmd);

    /** 查订单（读操作，走 GetOrderQryExe） */
    SingleResponse<OrderCO> getOrder(GetOrderQry qry);

    /** 订单操作：支付 / 发货 / 确认 / 取消 / 改备注（走 OrderOperateCmdExe -> 状态机） */
    SingleResponse<OrderCO> operateOrder(OrderOperateCmd cmd);
}
