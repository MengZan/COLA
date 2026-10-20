package com.example.cola.order.client.dto;

import com.alibaba.cola.dto.ClientObject;

/** 订单 Client Object：回给调用方的视图对象，领域实体不直接暴露。 */
public class OrderCO extends ClientObject {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String buyerId;
    private String itemName;
    private int quantity;
    /** 订单金额，单位：分 */
    private long totalAmountCent;
    /** 实付金额，单位：分 */
    private long payAmountCent;
    /** 命中优惠说明，如「88VIP专享85折」 */
    private String benefitLabel;
    /** 状态编码，如 WAIT_PAY */
    private String state;
    /** 状态中文描述，如 待支付 */
    private String stateDesc;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTotalAmountCent() {
        return totalAmountCent;
    }

    public void setTotalAmountCent(long totalAmountCent) {
        this.totalAmountCent = totalAmountCent;
    }

    public long getPayAmountCent() {
        return payAmountCent;
    }

    public void setPayAmountCent(long payAmountCent) {
        this.payAmountCent = payAmountCent;
    }

    public String getBenefitLabel() {
        return benefitLabel;
    }

    public void setBenefitLabel(String benefitLabel) {
        this.benefitLabel = benefitLabel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }
}
