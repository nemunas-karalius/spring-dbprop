package com.github.nemunaskaralius.spring.scope.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.aop.TargetSource;

import java.time.Duration;
import java.util.function.Supplier;

public class CaffeineCachedTargetSource implements TargetSource {
    private static final String CACHE_KEY = "key";

    private final LoadingCache<String, Object> cache;
    private final Class<?> targetClass;

    public CaffeineCachedTargetSource(Supplier<Object> beanLoader, Duration reloadInterval) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(reloadInterval)
                .build(k -> beanLoader.get());
        // determine target class once, during initialization:
        // getTargetClass() is invoked on every proxy access
        // BUT we do not expect it to change dynamically
        this.targetClass = getTarget().getClass();
    }

    @Override
    public Object getTarget() {
        return cache.get(CACHE_KEY);
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }
}