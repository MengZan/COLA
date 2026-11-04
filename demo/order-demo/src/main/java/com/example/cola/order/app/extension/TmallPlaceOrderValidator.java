package com.example.cola.order.app.extension;

import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.extension.Extension;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.dto.ErrorCode;
import com.example.cola.order.client.dto.PlaceOrderCmd;

/** 天猫校验：坐标为 tmall.placeOrder.#defaultScenario#，对任意 scenario 兜底。 */
@Extension(bizId = "tmall", useCase = "placeOrder")
public class TmallPlaceOrderValidator implements PlaceOrderValidatorExtPt {

    private final Logger logger = LoggerFactory.getLogger(TmallPlaceOrderValidator.class);

    @Override
    public void validate(PlaceOrderCmd cmd) {
        if (cmd.getQuantity() <= 0) {
            throw new BizException(ErrorCode.B_ORDER_QUANTITY_INVALID,
                    ErrorCode.B_ORDER_QUANTITY_INVALID.getErrDesc());
        }
        if (cmd.getAddress() == null || cmd.getAddress().trim().isEmpty()) {
            throw new BizException(ErrorCode.B_ORDER_ADDRESS_EMPTY,
                    ErrorCode.B_ORDER_ADDRESS_EMPTY.getErrDesc());
        }
        logger.info("[扩展实现] TmallPlaceOrderValidator: 天猫校验通过（数量 > 0 且地址必填）");
    }
}
