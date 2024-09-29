package com.github.nemunaskaralius.spring.scope.autorefresh;

import com.github.nemunaskaralius.spring.scope.cache.CaffeineCachedTargetSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.ClassUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Spring beans scope somewhat similar to {@link org.springframework.cloud.context.scope.refresh.RefreshScope}
 * but reloading beans automatically during bean access after predefined cache expiration period.
 */
//Code inspired by / copied from:
// https://www.baeldung.com/spring-custom-scope
// https://github.com/spring-cloud/spring-cloud-commons/blob/main/spring-cloud-context/src/main/java/org/springframework/cloud/context/scope/GenericScope.java
// https://github.com/spring-cloud/spring-cloud-commons/blob/main/spring-cloud-context/src/main/java/org/springframework/cloud/context/scope/refresh/RefreshScope.java
public class AutoRefreshScope implements Scope {
    private static final Logger log = LoggerFactory.getLogger(AutoRefreshScope.class);

    private final Duration refreshInterval;
    private final Map<String, Object> scopedObjects = new ConcurrentHashMap<>();
    private final Map<String, Runnable> destructionCallbacks = new ConcurrentHashMap<>();

    public AutoRefreshScope(Duration refreshInterval) {
        this.refreshInterval = refreshInterval;
        log.info("Auto-refresh scope initialised with refresh interval {}", refreshInterval);
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return scopedObjects.computeIfAbsent(name, key -> {
            // objectFactory.getObject() constructs requested bean
            // If bean is marked with @ConfigurationProperties annotation,
            // it is initialised with data from properties/yaml files inside objectFactory.getObject() call
            // with the help of ConfigurationPropertiesBindingPostProcessor...
            return createProxy(objectFactory::getObject);
        });
    }

    /**
     * Proxy creation logic copied from {@link org.springframework.aop.scope.ScopedProxyFactoryBean#setBeanFactory}.
     * <p>P.s. also tried to create proxy this way - not working with simple spring boot application:
     * <pre>
     * {@code
     * ScopedProxyFactoryBean proxyFactoryBean = new ScopedProxyFactoryBean();
     * proxyFactoryBean.setTargetBeanName(name);
     * // This line throws an exception:
     * // Not running in a ConfigurableBeanFactory: org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@4f25b795, started on Sat Sep 14 14:57:03 EEST 2024
     * proxyFactoryBean.setBeanFactory(applicationContext);
     * }
     * </pre>
     */
    private Object createProxy(Supplier<Object> beanLoader) {
        ProxyFactory pf = new ProxyFactory();
        //proxy as class - we do not expect property POJOs to be based on interfaces
        pf.setProxyTargetClass(true);
        var targetSource = new CaffeineCachedTargetSource(beanLoader, refreshInterval);
        pf.setTargetSource(targetSource);

        //TODO: hide AOP specific methods from generated JSONS somehow?
        // proxies implement these interfaces:
        // 	org.springframework.aop.framework.AopInfrastructureBean
        //	org.springframework.aop.SpringProxy
        //	org.springframework.aop.framework.Advised
        //	org.springframework.core.DecoratingProxy
        //NOTE: can not remove interfaces from proxy this way
        // pf.removeInterface(org.springframework.aop.framework.Advised.class);
        // pf.removeInterface(org.springframework.core.DecoratingProxy.class);

        // Add the AopInfrastructureBean marker to indicate that the scoped proxy
        // itself is not subject to auto-proxying! Only its target bean is.
        pf.addInterface(AopInfrastructureBean.class);

        return pf.getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object remove(String name) {
        destructionCallbacks.remove(name);
        return scopedObjects.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbacks.put(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
