package com.lfy.kcat.user.cache;


import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Reflective
public @interface CacheData {
    //
    String cacheKey() default "";

    String bloomFilterName() default "";
}
