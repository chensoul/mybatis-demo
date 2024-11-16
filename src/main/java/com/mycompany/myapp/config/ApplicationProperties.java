package com.mycompany.myapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.web.cors.CorsConfiguration;

@Data
@ConfigurationProperties("application")
public class ApplicationProperties {

    private final CorsConfiguration cors = new CorsConfiguration();

    @NestedConfigurationProperty
    private Logging logging = new Logging();

    @Data
    public static class Logging {
        private final Loki loki = new Loki();
        private boolean useJsonFormat = false;

        @Data
        public static class Loki {
            private String url = "http://localhost:3100/loki/api/v1/push";
            private String labelPattern = "application=${appName},host=${HOSTNAME},level=%level";
            private String messagePattern = "%level %logger{36} %thread | %msg %ex";
        }
    }
}
