package com.example.cola.order.app.extension;

import com.alibaba.cola.extension.Extension;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/** 天猫普通价格：坐标 tmall.placeOrder.#defaultScenario#，为 88vip 之外的天猫场景兜底。 */
@Extension(bizId = "tmall", useCase = "placeOrder")
public class TmallPriceCalc implements PriceCalcExtPt {

    private final Logger logger = LoggerFactory.getLogger(TmallPriceCalc.class);

    @Override
    public PriceResult calc(PlaceOrderCmd cmd) {
        logger.info("[扩展实现] TmallPriceCalc: 天猫会员 95折");
        return new PriceResult(cmd.totalAmountCent() * 95 / 100, "天猫会员95折");
    }
}
