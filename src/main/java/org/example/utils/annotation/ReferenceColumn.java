package org.example.utils.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReferenceColumn {
    int col() default 0;
    String value() default "";
}
