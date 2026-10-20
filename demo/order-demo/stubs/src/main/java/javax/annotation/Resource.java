/*
 * 【Demo 编译桩】替代 javax.annotation.Resource。
 * JDK 8 的 rt.jar 自带该注解，JDK 11+ 已移除；提供此桩可让 Demo 在任何 JDK 8+ 上编译。
 * 真实工程里由 JDK/Spring 环境提供，不需要这个文件。
 */
package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Resource {
    String name() default "";

    Class<?> type() default Object.class;
}
