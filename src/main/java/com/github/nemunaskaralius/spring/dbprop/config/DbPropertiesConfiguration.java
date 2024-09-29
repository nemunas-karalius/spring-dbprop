package com.github.nemunaskaralius.spring.dbprop.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.spring.ConfigurationPropertySource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DbPropertiesConfiguration {

    private final ConfigurableEnvironment env;
    private final DataSource dataSource;

    /**
     * Adding of property sources in @PostConstruct copied from https://gist.github.com/jeffsheets/8ab5f3aeb74787bdb051
     * <br>Such initialization allows to use JPA default DataSource which is initialized using application.properties
     * <p>
     * Rejected ways to add new property source:
     * <li>In ApplicationContextInitializer, see:
     * https://stackoverflow.com/questions/33714491/whats-the-best-way-to-add-a-new-property-source-in-spring
     * https://stackoverflow.com/questions/35217354/how-to-add-custom-applicationcontextinitializer-to-a-spring-boot-application
     * <br>Drawback: can not inject default JPA DataSource
     * <li>Using PropertySourcesPlaceholderConfigurer, see:
     * https://commons.apache.org/proper/commons-configuration/userguide/howto_utilities.html -> Use Configuration in Spring
     * <br>Drawback: fails to inject default JPA datasource - datasource construction fails with error:
     * <code>Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.</code>
     * (most probably no properties loaded at that stage?)
     * <p>
     */
    @PostConstruct
    public void initializeDatabasePropertySourceUsage() {
        MutablePropertySources propertySources = env.getPropertySources();

        try {
            BasicConfigurationBuilder<DatabaseConfiguration> builder = new BasicConfigurationBuilder<>(DatabaseConfiguration.class);
            builder.configure(
                    new Parameters().database()
                            .setDataSource(dataSource)
                            .setTable("properties")
                            .setKeyColumn("id")
                            .setValueColumn("val")
            );
            DatabaseConfiguration config = builder.getConfiguration();
            propertySources.addFirst(new ConfigurationPropertySource("db", config));
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }


    //-- rejected version with PropertySourcesPlaceholderConfigurer (could not inject a JPA datasource) --
//    @Bean
//    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(ConfigurableEnvironment env, DataSource dataSource)
//            throws ConfigurationException {
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        MutablePropertySources sources = new MutablePropertySources();
//        for (var source : env.getPropertySources()) {
//            sources.addLast(source);
//        }
//
//        Map<String, Object> myProperties = new HashMap<>();
//        myProperties.put("sample.someString", "custom-value");
//        sources.addLast(new MapPropertySource("my-props", myProperties));
//
//        configurer.setPropertySources(sources);
//        configurer.setEnvironment(env);
//        return configurer;
//    }

}
