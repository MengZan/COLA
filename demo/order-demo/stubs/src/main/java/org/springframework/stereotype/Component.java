/*
 * 【Demo 编译桩】替代 Spring 的 @Component。
 * 真实 COLA 工程里依赖 cola-core + Spring，不需要这个文件。
 */
package org.springframework.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Component {
    String value() default "";
}
