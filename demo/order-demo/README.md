# COLA 订单 Demo（可运行）

一个最小但**真实使用本仓库 COLA 源码**的下单示例，用来演示四件事：

1. **扩展点（Extension）**：同样下单 1000 元，`tmall.placeOrder.88vip` 走 85 折、`tmall.placeOrder.normal` 兜底到 95 折、`taobao.placeOrder.normal` 两级兜底到淘宝的「满 200 减 20」；校验规则也按场景分流（天猫必填地址、淘宝可不填）。
2. **轻量 CQRS + 分层**：`OrderServiceI` 只转发；写走 `executor/*CmdExe`，读走 `executor/query/*QryExe`；层与层之间靠 CO / Entity / Gateway 隔离。
3. **领域事件与统一异常**：下单成功由 `EventBus.fire(OrderCreatedEvent)` 触发 `@EventHandler` 处理器；`BizException(错误码)` 在服务层经 COLA 自带的 `DefaultExceptionHandler` 收敛成 `Response(errCode, errMessage)`。
4. **状态机**：用 `cola-statemachine` 的 Fluent DSL 定义订单生命周期（支付/发货/确认/取消），并生成 PlantUML 状态图。

编进来的 COLA 源码（`run.sh` 里有完整清单，未改一行原仓库代码）：

- 全部 `cola-framework-common`（DTO 规范、`BizScenario`、事件接口）
- `cola-framework-core` 节选 29 个类（`ExtensionExecutor` / `ExtensionRegister` / `EventRegister` / `EventBus` / 异常 / 日志门面）
- 全部 `cola-statemachine`

## 运行

只需要一套 JDK 8+（无 Maven、无网络、无数据库）：

```bash
cd demo/order-demo
./run.sh          # 若 javac 不在 PATH，请先 export JAVA_HOME=/path/to/jdk
```

`run.sh` 会用 `javac -encoding UTF-8` 把仓库源码子集 + `stubs/` + 本 Demo 一起编译到 `target/`，然后运行演示并执行断言（任意断言失败以非 0 退出码结束）。运行后生成 `order-state-machine.puml`，可粘贴到 [plantuml.com](https://www.plantuml.com/plantuml/) 或 IDEA 的 PlantUML 插件查看。

> 为什么不用 Maven？上游 `pom.xml` 把 gpg 签名绑定在默认 `verify` 阶段且年代久远，本地构建步骤繁琐；对这个 Demo 而言 `javac` 直编同一批源码最直观。本 Demo 已在 OpenJDK 1.8.0_152（AOSP 预编译 JDK）上验证通过；源码只使用 Java 8 语言特性，其它 JDK 8+ 理论可行（`stubs` 已补齐高版本 JDK 里移走的 `javax.annotation.Resource`），但未逐版本验证。

## 目录结构与 COLA 模块的对应关系

Demo 为方便 `javac` 直编收成单一源码树，包结构与脚手架生成的模块一一对应：

```
src/main/java/com/example/cola/order
├── client/             == 脚手架 -client：api 接口、Cmd/Qry、CO、领域事件、错误码
├── app/
│   ├── service/        == -app：ServiceImpl（薄转发 + 统一异常收敛）
│   ├── executor/       == -app：*CmdExe / query/*QryExe（用例编排）
│   ├── extension/      == -app：两个扩展点（*ExtPt）与 5 个 @Extension 实现
│   ├── eventhandler/   == -app：@EventHandler 处理器
│   └── convertor/      == -app：Entity -> CO 转换
├── domain/             == -domain：@Entity 订单、订单状态机、Gateway 接口
├── infrastructure/     == -infrastructure：Gateway 内存实现、手写装配（见下）
└── start/              == start：Main（演示入口）
stubs/                  编译桩（见下）
```

## 为了「开箱即跑」做的两处简化（与真实 COLA 工程的差异）

1. **手动装配代替 Spring**：`infrastructure/config/Bootstrap.java` 用显式 `new` + 一次反射字段注入组装对象图。它仍复用真实的 `ExtensionRegister`/`EventRegister`（解析 `@Extension` 坐标、校验 `*ExtPt` 命名、查重、反射事件参数），差别仅是真实工程里由 Spring 组件扫描找出这些 Bean，再由 `SpringBootstrap.init()` 交给同一个注册器。真实工程的对应代码就是在 `ColaConfig` 里放一个 `@Bean(initMethod = "init") SpringBootstrap`。
2. **`stubs/` 编译桩**：为能在无 Maven/无中央仓库的环境编译 COLA 源码，`stubs/` 提供了 `@Component`/`@Autowired`/`ApplicationContext` 等 Spring 与 SLF4J 接口的最小声明。**真实工程删除整个 `stubs/`、依赖真正的 Spring/SLF4J 即可，Demo 业务代码不需要改动。**
3. 持久层用内存 `Map`（`InMemoryOrderGateway`）代替 MyBatis+DO；实体直接 `new`，未走 `DomainFactory.create()` 的 prototype Bean 机制——这两个都在接口层留好隔离，替换实现不影响上层。

## 读代码的建议路线

- `client/dto/PlaceOrderCmd`（命令携带 `BizScenario`） → `app/executor/PlaceOrderCmdExe`（标准四步编排） → `app/extension/*`（两个扩展点的坐标与兜底） → 运行日志里 `ExtensionExecutor` 打出的 First/Second/Third trying 路径 → `domain/order/OrderStateMachines`（DSL 定义） 与 `domain/order/Order#operate`（非法流动的守卫） → `infrastructure/config/Bootstrap`（框架如何把一切装配起来）。

## 两个写进代码里的真实注意点

- `BizScenario.getIdentityWithDefault*()` 是**带副作用的 getter**：扩展兜底时它会把当前对象的 scenario/useCase 就地改成默认值。一个 Cmd 内连续调用两次扩展点时，第二次会拿到被「降级」的坐标——`PlaceOrderCmdExe` 对此做了捕获+克隆规避（代码里有注释）。
- `cola-statemachine` 在「无匹配转换」或「条件不满足」时**停留在原状态而不报错**，与内部转换（本 Demo 的 `UPDATE_REMARK`）结果看上去一样，都是"没变"。`Order#operate` 因此用事件类型区分，并把非法操作统一抛成业务错误码 `B_ORDER_stateIllegal`。
