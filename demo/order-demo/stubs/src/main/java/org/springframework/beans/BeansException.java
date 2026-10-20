/* 【Demo 编译桩】替代 Spring 的 BeansException。真实工程里不需要。 */
package org.springframework.beans;

public abstract class BeansException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
