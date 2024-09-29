package com.github.nemunaskaralius.spring.scope.annotation;

import org.springframework.context.annotation.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Scope("auto-refresh")
@Documented
public @interface AutoRefreshScope {

    //TODO: maybe implement ScopedProxyMode proxyMode() ?
    // BUT: when this would be useful? e.g. we do not expect property POJOs to be based on interfaces...
    //see: https://github.com/spring-cloud/spring-cloud-commons/blob/main/spring-cloud-context/src/main/java/org/springframework/cloud/context/config/annotation/RefreshScope.java
}
