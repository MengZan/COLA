/* 【Demo 编译桩】替代 SLF4J 的 LoggerFactory：给 COLA 的日志门面返回一个最简单的控制台 Logger。真实工程里不需要。 */
package org.slf4j;

public final class LoggerFactory {

    private LoggerFactory() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return new ConsoleLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        return new ConsoleLogger(name);
    }

    private static class ConsoleLogger implements Logger {
        private final String name;

        ConsoleLogger(String name) {
            this.name = name;
        }

        public void debug(String msg) {
            System.out.println("[" + name + "] DEBUG: " + msg);
        }

        public void info(String msg) {
            System.out.println("[" + name + "] INFO: " + msg);
        }

        public void warn(String msg) {
            System.out.println("[" + name + "] WARN: " + msg);
        }

        public void error(String msg) {
            System.err.println("[" + name + "] ERROR: " + msg);
        }

        public void error(String msg, Throwable t) {
            System.err.println("[" + name + "] ERROR: " + msg);
            t.printStackTrace();
        }
    }
}
