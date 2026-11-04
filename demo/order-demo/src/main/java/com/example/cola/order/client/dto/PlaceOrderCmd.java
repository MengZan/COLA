package com.example.cola.order.client.dto;

import com.alibaba.cola.dto.Command;

/**
 * 下单命令。继承 COLA 的 Command，天然携带 BizScenario（bizId + useCase + scenario），
 * 扩展点定位就靠它完成。
 */
public class PlaceOrderCmd extends Command {

    private static final long serialVersionUID = 1L;

    private String buyerId;
    private String itemName;
    private int quantity;
    /** 单价，单位：分 */
    private long unitPriceCent;
    private String address;

    /** 订单总额，单位：分 */
    public long totalAmountCent() {
        return unitPriceCent * quantity;
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

    public long getUnitPriceCent() {
        return unitPriceCent;
    }

    public void setUnitPriceCent(long unitPriceCent) {
        this.unitPriceCent = unitPriceCent;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
