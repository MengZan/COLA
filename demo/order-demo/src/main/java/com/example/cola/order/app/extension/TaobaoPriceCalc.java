package com.example.cola.order.app.extension;

import com.alibaba.cola.extension.Extension;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/** 淘宝价格：只声明 bizId，坐标 taobao.#defaultUseCase#.#defaultScenario#，靠两级兜底命中。满 200 减 20。 */
@Extension(bizId = "taobao")
public class TaobaoPriceCalc implements PriceCalcExtPt {

    private final Logger logger = LoggerFactory.getLogger(TaobaoPriceCalc.class);

    @Override
    public PriceResult calc(PlaceOrderCmd cmd) {
        long total = cmd.totalAmountCent();
        if (total >= 20000) {
            logger.info("[扩展实现] TaobaoPriceCalc: 满200减20");
            return new PriceResult(total - 2000, "淘宝满200减20");
        }
        logger.info("[扩展实现] TaobaoPriceCalc: 无优惠");
        return new PriceResult(total, "无优惠");
    }
}
