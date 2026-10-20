package com.example.cola.order.app.extension;

import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.extension.Extension;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.ErrorCode;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/**
 * 淘宝校验：只声明 bizId，坐标为 taobao.#defaultUseCase#.#defaultScenario#。
 * 当请求带着 taobao.placeOrder.normal 进来时，ExtensionExecutor 会逐级兜底，第三跳才命中这里（看运行日志）。
 */
@Extension(bizId = "taobao")
public class TaobaoPlaceOrderValidator implements PlaceOrderValidatorExtPt {

    private final Logger logger = LoggerFactory.getLogger(TaobaoPlaceOrderValidator.class);

    @Override
    public void validate(PlaceOrderCmd cmd) {
        if (cmd.getQuantity() <= 0) {
            throw new BizException(ErrorCode.B_ORDER_QUANTITY_INVALID,
                    ErrorCode.B_ORDER_QUANTITY_INVALID.getErrDesc());
        }
        logger.info("[扩展实现] TaobaoPlaceOrderValidator: 淘宝校验通过（仅校验数量，支持自提可不填地址）");
    }
}
