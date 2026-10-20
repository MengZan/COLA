package com.example.cola.order.client.dto;

/** 对外暴露的操作类型（在 App 层会被映射成领域层的状态机事件） */
public enum OperateType {
    PAY,
    SHIP,
    CONFIRM_RECEIPT,
    CANCEL,
    UPDATE_REMARK
}
