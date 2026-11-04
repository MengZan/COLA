package com.example.cola.order.infrastructure.config;

import com.alibaba.cola.boot.EventRegister;
import com.alibaba.cola.boot.ExtensionRegister;
import com.alibaba.cola.event.EventBus;
import com.alibaba.cola.event.EventHub;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.cola.extension.ExtensionRepository;
import com.alibaba.cola.statemachine.StateMachine;
import com.example.cola.order.app.eventhandler.OrderCreatedHandler;
import com.example.cola.order.app.executor.OrderOperateCmdExe;
import com.example.cola.order.app.executor.PlaceOrderCmdExe;
import com.example.cola.order.app.executor.query.GetOrderQryExe;
import com.example.cola.order.app.extension.*;
import com.example.cola.order.app.service.OrderServiceImpl;
import com.example.cola.order.client.api.OrderServiceI;
import com.example.cola.order.domain.gateway.OrderGateway;
import com.example.cola.order.domain.order.Order;
import com.example.cola.order.domain.order.OrderState;
import com.example.cola.order.domain.order.OrderStateEvent;
import com.example.cola.order.domain.order.OrderStateMachines;
import com.example.cola.order.infrastructure.gatewayimpl.InMemoryOrderGateway;

import java.lang.reflect.Field;

/**
 * 【Demo 的手动装配类】代替 Spring 容器 + cola-framework-core 的 SpringBootstrap。
 *
 * 它完整复用了真实的 ExtensionRegister / EventRegister / ExtensionExecutor / EventBus：
 * 差别仅在真实工程由 Spring 组件扫描把 @Extension、@EventHandler 的 Bean 找出来，
 * 这里改为显式 new 出来交给同一个注册器。字段注入也用一次反射代替 @Resource。
 *
 * 真实 COLA 工程里这一切被 ColaConfig 替代：
 *   @Bean(initMethod = "init") public SpringBootstrap bootstrap() { return new SpringBootstrap(); }
 */
public class Bootstrap {

    private final OrderServiceI orderService;
    private final StateMachine<OrderState, OrderStateEvent, Order> stateMachine;

    public Bootstrap() {
        // —— 扩展点注册（真实 ExtensionRegister：解析 @Extension 坐标、校验 ExtPt 命名、查重）——
        ExtensionRepository extensionRepository = new ExtensionRepository();
        ExtensionRegister extensionRegister = new ExtensionRegister();
        inject(extensionRegister, "extensionRepository", extensionRepository);
        extensionRegister.doRegistration(new TmallPlaceOrderValidator());
        extensionRegister.doRegistration(new TaobaoPlaceOrderValidator());
        extensionRegister.doRegistration(new Tmall88VipPriceCalc());
        extensionRegister.doRegistration(new TmallPriceCalc());
        extensionRegister.doRegistration(new TaobaoPriceCalc());

        ExtensionExecutor extensionExecutor = new ExtensionExecutor();
        inject(extensionExecutor, "extensionRepository", extensionRepository);

        // —— 领域事件注册（真实 EventRegister：反射 execute 参数类型找到事件类）——
        EventHub eventHub = new EventHub();
        EventRegister eventRegister = new EventRegister();
        inject(eventRegister, "eventHub", eventHub);
        eventRegister.doRegistration(new OrderCreatedHandler());

        EventBus eventBus = new EventBus();
        inject(eventBus, "eventHub", eventHub);

        // —— 业务对象组装 ——
        OrderGateway orderGateway = new InMemoryOrderGateway();
        this.stateMachine = OrderStateMachines.create();
        this.orderService = new OrderServiceImpl(
                new PlaceOrderCmdExe(extensionExecutor, orderGateway, eventBus),
                new GetOrderQryExe(orderGateway),
                new OrderOperateCmdExe(orderGateway, stateMachine));
    }

    public OrderServiceI getOrderService() {
        return orderService;
    }

    public StateMachine<OrderState, OrderStateEvent, Order> getStateMachine() {
        return stateMachine;
    }

    private static void inject(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException("注入字段失败: " + target.getClass().getName() + "." + fieldName, e);
        }
    }
}
