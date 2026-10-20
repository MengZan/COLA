/* 【Demo 编译桩】替代 SLF4J 的 org.slf4j.Logger（仅声明 COLA 的 SLFJLogger 用到的方法）。真实工程里不需要。 */
package org.slf4j;

public interface Logger {
    void debug(String msg);

    void info(String msg);

    void warn(String msg);

    void error(String msg);

    void error(String msg, Throwable t);
}
