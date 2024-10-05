package com.github.nemunaskaralius.spring.dbprop.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import javax.sql.DataSource;

/**
 * Plain JDBC based configuration class for retrieving Spring properties from a DB.
 * Use @Import to add to your application...
 */
@ConditionalOnProperty(name = "spring.properties.db.source.impl", havingValue = "jdbc")
@Configuration
@RequiredArgsConstructor
public class JdbcDbPropertiesConfiguration {

    private final ConfigurableEnvironment env;
    private final DataSource dataSource;
    private final DbPropertySourceProperties dbSourceProperties;

    @PostConstruct
    public void initializeDatabasePropertySourceUsage() {
        MutablePropertySources propertySources = env.getPropertySources();
        propertySources.addFirst(new JdbcPropertySource("db", dataSource, dbSourceProperties));
    }
}
