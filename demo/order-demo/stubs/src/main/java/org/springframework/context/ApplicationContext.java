/* 【Demo 编译桩】替代 Spring 的 ApplicationContext（仅声明 ApplicationContextHelper 用到的方法）。真实工程里不需要。 */
package org.springframework.context;

public interface ApplicationContext {
    <T> T getBean(Class<T> requiredType);

    Object getBean(String name);
}
