package com.example.cola.order.client.dto;

import com.alibaba.cola.dto.Command;

/** 订单操作命令：支付 / 发货 / 确认收货 / 取消 / 改备注。最终由领域实体内部的状态机守卫合法转换。 */
public class OrderOperateCmd extends Command {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private OperateType operateType;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }
}
