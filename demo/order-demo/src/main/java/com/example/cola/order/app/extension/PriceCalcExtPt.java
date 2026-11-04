package com.example.cola.order.app.extension;

import com.alibaba.cola.extension.ExtensionPointI;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/**
 * 扩展点②：价格计算。天猫 88VIP、天猫普通用户、淘宝用户分别走三套规则；
 * 定位坐标就是 Cmd 上的 BizScenario（bizId + useCase + scenario）。
 */
public interface PriceCalcExtPt extends ExtensionPointI {

    PriceResult calc(PlaceOrderCmd cmd);
}
