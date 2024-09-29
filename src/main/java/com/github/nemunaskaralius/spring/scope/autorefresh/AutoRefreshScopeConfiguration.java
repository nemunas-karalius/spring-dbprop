package com.github.nemunaskaralius.spring.scope.autorefresh;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
//TODO: could extract this to a separate lib and convert to @AutoConfiguration
// see as an example:
// https://github.com/spring-cloud/spring-cloud-commons/blob/main/spring-cloud-context/src/main/java/org/springframework/cloud/autoconfigure/RefreshAutoConfiguration.java
public class AutoRefreshScopeConfiguration {

    @Bean
    public static AutoRefreshScopePostProcessor autoRefreshScopePostProcessor(
            @Value("${spring.auto-refresh-scope.refresh-interval:PT1S}") Duration refreshInterval
    ) {
        return new AutoRefreshScopePostProcessor(new AutoRefreshScope(refreshInterval));
    }

    public static class AutoRefreshScopePostProcessor implements BeanFactoryPostProcessor {
        private final AutoRefreshScope autoRefreshScope;

        public AutoRefreshScopePostProcessor(AutoRefreshScope autoRefreshScope) {
            this.autoRefreshScope = autoRefreshScope;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
            factory.registerScope("auto-refresh", autoRefreshScope);
        }
    }
}
