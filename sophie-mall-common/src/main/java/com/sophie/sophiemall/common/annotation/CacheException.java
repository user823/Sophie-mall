package com.sophie.sophiemall.common.annotation;

import java.lang.annotation.*;

/**
 * 添加该注解的缓存方法会抛出异常
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheException {
}
