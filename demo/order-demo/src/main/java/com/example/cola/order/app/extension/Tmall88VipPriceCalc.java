package com.example.cola.order.app.extension;

import com.alibaba.cola.extension.Extension;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/** 天猫 88VIP 价格：坐标 tmall.placeOrder.88vip，最具体，优先命中。 */
@Extension(bizId = "tmall", useCase = "placeOrder", scenario = "88vip")
public class Tmall88VipPriceCalc implements PriceCalcExtPt {

    private final Logger logger = LoggerFactory.getLogger(Tmall88VipPriceCalc.class);

    @Override
    public PriceResult calc(PlaceOrderCmd cmd) {
        logger.info("[扩展实现] Tmall88VipPriceCalc: 88VIP 85折");
        return new PriceResult(cmd.totalAmountCent() * 85 / 100, "88VIP专享85折");
    }
}
