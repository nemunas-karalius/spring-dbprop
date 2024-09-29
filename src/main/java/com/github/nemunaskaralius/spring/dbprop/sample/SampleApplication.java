package com.github.nemunaskaralius.spring.dbprop.sample;

import com.github.nemunaskaralius.spring.scope.autorefresh.AutoRefreshScopeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = "com.github.nemunaskaralius.spring.dbprop")
@ConfigurationPropertiesScan
// AutoRefreshScopeConfiguration we have in the separate package not specified in @ComponentScan above
@Import({AutoRefreshScopeConfiguration.class})
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
