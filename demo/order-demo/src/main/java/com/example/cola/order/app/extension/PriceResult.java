package com.example.cola.order.app.extension;

/** 价格扩展点的返回结果。 */
public class PriceResult {

    /** 实付金额，单位：分 */
    private final long payAmountCent;
    /** 命中优惠说明 */
    private final String benefitLabel;

    public PriceResult(long payAmountCent, String benefitLabel) {
        this.payAmountCent = payAmountCent;
        this.benefitLabel = benefitLabel;
    }

    public long getPayAmountCent() {
        return payAmountCent;
    }

    public String getBenefitLabel() {
        return benefitLabel;
    }
}
