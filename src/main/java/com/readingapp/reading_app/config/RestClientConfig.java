package com.readingapp.reading_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/*Crea un bean RestTemplate que permite hacer peticiones HTTP a APIs externas*/
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Sin límite, una API externa lenta dejaría la petición del usuario colgada indefinidamente
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return new RestTemplate(factory);
    }
}
