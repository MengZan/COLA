/* 【Demo 编译桩】替代 Spring 的 ApplicationContextAware。真实工程里不需要。 */
package org.springframework.context;

import org.springframework.beans.BeansException;

public interface ApplicationContextAware {
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
