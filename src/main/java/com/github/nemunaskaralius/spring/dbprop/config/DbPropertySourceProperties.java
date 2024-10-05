package com.github.nemunaskaralius.spring.dbprop.config;

import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Configuration of the DB table for storing spring properties
 */
@Data
@Configuration
@ConfigurationProperties(DbPropertySourceProperties.PREFIX)
public class DbPropertySourceProperties {
    public static final String PREFIX = "spring.properties.db.source";

    private String tableName;
    private String keyColumn;
    private String valueColumn;

    public void validate() {
        Validate.validState(isNotBlank(tableName), "%s.tableName should not be blank", PREFIX);
        Validate.validState(isNotBlank(keyColumn), "%s.keyColumn should not be blank", PREFIX);
        Validate.validState(isNotBlank(valueColumn), "%s.valueColumn should not be blank", PREFIX);
    }
}
