package com.example.cola.order.domain.order;

import com.alibaba.cola.domain.Entity;
import com.alibaba.cola.domain.EntityObject;
import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.statemachine.StateMachine;
import com.example.cola.order.client.dto.ErrorCode;

/**
 * 订单领域实体（充血模型）。
 *
 * COLA 的 @Entity 本质是 @Component + prototype 作用域：真实工程里通过
 * DomainFactory.create(Order.class) 拿到能注入协作对象的全新实例；本 Demo 手写 DI，直接 new。
 * 状态变更统一委托给 cola-statemachine，实体只负责解释结果是否合法。
 */
@Entity
public class Order extends EntityObject {

    private String orderId;
    private String buyerId;
    private String itemName;
    private int quantity;
    /** 订单金额，单位：分 */
    private long totalAmountCent;
    /** 实付金额，单位：分（由价格扩展点算出） */
    private long payAmountCent;
    private String benefitLabel;
    private OrderState state = OrderState.WAIT_PAY;

    public Order(String buyerId, String itemName, int quantity,
                 long totalAmountCent, long payAmountCent, String benefitLabel) {
        this.buyerId = buyerId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmountCent = totalAmountCent;
        this.payAmountCent = payAmountCent;
        this.benefitLabel = benefitLabel;
    }

    /**
     * 执行一次状态迁移。
     *
     * 注意 cola-statemachine 的约定：没有匹配转换、或条件（Condition）不满足时，
     * fireEvent 会停留在原状态并返回原状态（不抛异常）；但 UPDATE_REMARK 是内部转换，
     * 成功时也停留在原状态。所以除了 UPDATE_REMARK，其它操作若状态没变就视为非法，抛业务异常。
     */
    public void operate(StateMachine<OrderState, OrderStateEvent, Order> stateMachine, OrderStateEvent event) {
        OrderState before = this.state;
        OrderState target = stateMachine.fireEvent(before, event, this);
        if (target == before && event != OrderStateEvent.UPDATE_REMARK) {
            throw new BizException(ErrorCode.B_ORDER_STATE_ILLEGAL,
                    "订单 " + orderId + " 状态[" + before.getDesc() + "]不允许执行操作[" + event + "]");
        }
        this.state = target;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTotalAmountCent() {
        return totalAmountCent;
    }

    public long getPayAmountCent() {
        return payAmountCent;
    }

    public String getBenefitLabel() {
        return benefitLabel;
    }

    public OrderState getState() {
        return state;
    }
}
