/* 【Demo 编译桩】替代 Spring 的 ConfigurableBeanFactory（只取 COLA 用到的常量）。真实工程里不需要。 */
package org.springframework.beans.factory.config;

public interface ConfigurableBeanFactory {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
}
