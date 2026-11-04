package com.example.cola.order.app.extension;

import com.alibaba.cola.extension.ExtensionPointI;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/**
 * 扩展点①：下单校验。命名必须以 ExtPt 结尾并继承 ExtensionPointI，否则 ExtensionRegister 注册时直接报错。
 * 它表达「一块逻辑在不同业务下有不同实现」：天猫要求必填地址，淘宝支持自提可以不填。
 */
public interface PlaceOrderValidatorExtPt extends ExtensionPointI {

    void validate(PlaceOrderCmd cmd);
}
