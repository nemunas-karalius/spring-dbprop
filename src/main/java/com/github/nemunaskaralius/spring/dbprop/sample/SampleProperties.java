package com.github.nemunaskaralius.spring.dbprop.sample;

import com.github.nemunaskaralius.spring.scope.annotation.AutoRefreshScope;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@AutoRefreshScope
@Component //this is needed for the @AutoRefreshScope annotation to work
@ConfigurationProperties("sample")
public class SampleProperties {
    private String someString = "test";
    private int someInt = 1;
    private final Connection connection = new Connection();

    @Data
    public static class Connection {
        private String url;
        private Duration timeout = Duration.ofSeconds(30);
    }
}
