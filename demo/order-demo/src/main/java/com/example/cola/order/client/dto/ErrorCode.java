package com.example.cola.order.client.dto;

import com.alibaba.cola.dto.ErrorCodeI;

/** 业务错误码：实现 cola-common 的 ErrorCodeI，这样 BizException 能携带错误码一路透传到 Response。 */
public enum ErrorCode implements ErrorCodeI {

    B_ORDER_QUANTITY_INVALID("B_ORDER_quantityInvalid", "购买数量必须大于0"),

    B_ORDER_ADDRESS_EMPTY("B_ORDER_addressEmpty", "收货地址不能为空"),

    B_ORDER_NOT_FOUND("B_ORDER_notFound", "订单不存在"),

    B_ORDER_STATE_ILLEGAL("B_ORDER_stateIllegal", "当前订单状态不允许该操作");

    private final String errCode;
    private final String errDesc;

    ErrorCode(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public String getErrDesc() {
        return errDesc;
    }
}
