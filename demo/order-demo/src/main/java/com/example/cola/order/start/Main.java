package com.example.cola.order.start;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.logger.LoggerFactory;
import com.example.cola.order.client.api.OrderServiceI;
import com.example.cola.order.client.dto.*;
import com.example.cola.order.infrastructure.config.Bootstrap;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Demo 入口：用天猫 88VIP / 天猫普通 / 淘宝三种 BizScenario 下单，
 * 看扩展点的定位与逐级兜答；再驱动订单走完状态机生命周期。带断言，任一失败以非 0 码退出。
 */
public class Main {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        // Demo 专用：切到控制台日志，后面能直看到 ExtensionExecutor 的扩展定位路径
        LoggerFactory.activateSysLogger();

        Bootstrap bootstrap = new Bootstrap();
        OrderServiceI service = bootstrap.getOrderService();

        // ===== 1. 三个场景下单（同样的商品：1000 元机械键盘）=====
        banner("1. 多业务下单：扩展点按 BizScenario 分流计价 —— 天猫88VIP 85折 / 天猫 95折 / 淘宝满200减20");
        SingleResponse<OrderCO> a = service.placeOrder(placeCmd("tmall", "placeOrder", "88vip", "杭州市西湖区 xx 路 1 号"));
        SingleResponse<OrderCO> b = service.placeOrder(placeCmd("tmall", "placeOrder", "normal", "杭州市西湖区 xx 路 1 号"));
        SingleResponse<OrderCO> c = service.placeOrder(placeCmd("taobao", "placeOrder", "normal", null)); // 淘宝自提：不填地址
        printPlace("A. tmall.placeOrder.88vip ", a);
        printPlace("B. tmall.placeOrder.normal", b);
        printPlace("C. taobao.placeOrder.normal", c);
        check(a.isSuccess() && a.getData().getPayAmountCent() == 85000L, "A 88VIP 实付 850.00 元");
        check(b.isSuccess() && b.getData().getPayAmountCent() == 95000L, "B 天猫普通实付 950.00 元");
        check(c.isSuccess() && c.getData().getPayAmountCent() == 98000L, "C 淘宝满减实付 980.00 元（且校验规则更松，没地址也通过）");

        String orderA = a.getData().getOrderId();
        String orderB = b.getData().getOrderId();
        String orderC = c.getData().getOrderId();

        // ===== 2. 差异化校验差异 =====
        banner("2. 同样没填地址，天猫场景必须失败（错误码透传到 Response）");
        SingleResponse<OrderCO> d = service.placeOrder(placeCmd("tmall", "placeOrder", "normal", ""));
        System.out.println("   D. 天猫无地址下单 -> success=" + d.isSuccess() + ", errCode=" + d.getErrCode() + ", errMessage=" + d.getErrMessage());
        check(!d.isSuccess() && ErrorCode.B_ORDER_ADDRESS_EMPTY.getErrCode().equals(d.getErrCode()),
                "D 被天猫校验扩展拒绝，并通过 DefaultExceptionHandler 收敛成失败 Response");

        // ===== 3. CQRS 查询端 =====
        banner("3. 查询订单（Qry 链路）");
        GetOrderQry qry = new GetOrderQry();
        qry.setOrderId(orderA);
        SingleResponse<OrderCO> got = service.getOrder(qry);
        System.out.println("   查 " + orderA + " -> " + describe(got.getData()));

        // ===== 4. 订单生命周期（状态机守卫）=====
        banner("4. 订单生命周期：状态机守卫非法转换");
        SingleResponse<OrderCO> shipTooEarly = operate(service, orderA, OperateType.SHIP);
        System.out.println("   未支付直接发货 -> success=" + shipTooEarly.isSuccess() + ", errCode=" + shipTooEarly.getErrCode());
        check(!shipTooEarly.isSuccess() && ErrorCode.B_ORDER_STATE_ILLEGAL.getErrCode().equals(shipTooEarly.getErrCode()),
                "WAIT_PAY 状态不允许 SHIP，已被守卫并转成错误码");

        operate(service, orderA, OperateType.PAY);
        operate(service, orderA, OperateType.UPDATE_REMARK);
        operate(service, orderA, OperateType.SHIP);
        SingleResponse<OrderCO> done = operate(service, orderA, OperateType.CONFIRM_RECEIPT);
        check(done.isSuccess() && "COMPLETED".equals(done.getData().getState()), "订单 A 已走完：WAIT_PAY→PAID→SHIPPED→COMPLETED");

        SingleResponse<OrderCO> cancelled = operate(service, orderC, OperateType.CANCEL);
        check(cancelled.isSuccess() && "CANCELLED".equals(cancelled.getData().getState()), "订单 C 未支付取消 -> CANCELLED");

        operate(service, orderB, OperateType.PAY);
        SingleResponse<OrderCO> refunded = operate(service, orderB, OperateType.CANCEL);
        check(refunded.isSuccess() && "REFUNDED".equals(refunded.getData().getState()), "订单 B 已支付取消 -> REFUNDED（同事件不同源状态走向不同目标）");

        // ===== 5. 状态图 =====
        banner("5. cola-statemachine 生成的状态图（PlantUML）");
        String puml = bootstrap.getStateMachine().generatePlantUML();
        System.out.println(puml);
        Files.write(Paths.get("order-state-machine.puml"), puml.getBytes(StandardCharsets.UTF_8));
        System.out.println("（已写入 order-state-machine.puml，可粘到 plantuml.com 或 IDEA PlantUML 插件里看图；"
                + "cola-statemachine 是无状态设计，没有内置初始态，想标出入口可自己在 @startuml 后加一行：[*] --> WAIT_PAY）");

        banner(failures == 0 ? "演示结束 ✅ 全部断言通过" : "演示结束 ❌ 有 " + failures + " 条断言失败");
        if (failures > 0) {
            System.exit(1);
        }
    }

    private static PlaceOrderCmd placeCmd(String bizId, String useCase, String scenario, String address) {
        PlaceOrderCmd cmd = new PlaceOrderCmd();
        cmd.setBizScenario(BizScenario.valueOf(bizId, useCase, scenario));
        cmd.setBuyerId("u1001");
        cmd.setItemName("机械键盘");
        cmd.setQuantity(1);
        cmd.setUnitPriceCent(100000L); // 1000.00 元
        cmd.setAddress(address);
        return cmd;
    }

    private static SingleResponse<OrderCO> operate(OrderServiceI service, String orderId, OperateType type) {
        OrderOperateCmd cmd = new OrderOperateCmd();
        cmd.setOrderId(orderId);
        cmd.setOperateType(type);
        SingleResponse<OrderCO> r = service.operateOrder(cmd);
        if (r.isSuccess()) {
            System.out.println("   " + orderId + " " + type + " -> " + r.getData().getState() + "（" + r.getData().getStateDesc() + "）");
        }
        return r;
    }

    private static void printPlace(String title, SingleResponse<OrderCO> r) {
        if (r.isSuccess()) {
            OrderCO co = r.getData();
            System.out.println("   " + title + " -> 订单 " + co.getOrderId()
                    + "，原价 " + yuan(co.getTotalAmountCent()) + "，实付 " + yuan(co.getPayAmountCent())
                    + "，「" + co.getBenefitLabel() + "」，状态 " + co.getState() + "（" + co.getStateDesc() + "）");
        } else {
            System.out.println("   " + title + " -> 失败 errCode=" + r.getErrCode() + ", errMessage=" + r.getErrMessage());
        }
    }

    private static String describe(OrderCO co) {
        return co.getItemName() + " x" + co.getQuantity() + "，实付 " + yuan(co.getPayAmountCent())
                + "，「" + co.getBenefitLabel() + "」，状态 " + co.getState() + "（" + co.getStateDesc() + "）";
    }

    private static String yuan(long cent) {
        return String.format("%.2f 元", cent / 100.0);
    }

    private static void banner(String text) {
        System.out.println();
        System.out.println("==== " + text + " ====");
    }

    private static void check(boolean ok, String msg) {
        System.out.println((ok ? "   ✅ " : "   ❌ ") + msg);
        if (!ok) {
            failures++;
        }
    }
}
