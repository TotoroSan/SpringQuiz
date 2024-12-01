package com.example.quiz.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// for HTTP to HTTPS redicrection
@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory) {
                server.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
            }
        };
    }

    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);  // HTTP port for incoming requests
        connector.setSecure(false);
        connector.setRedirectPort(8443);  // Redirect to HTTPS port
        return connector;
    }
}