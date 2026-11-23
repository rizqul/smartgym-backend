package com.gymmick.smartgym.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.config.midtrans.base-url}")
    private String midtransBaseUrl;

    @Value("${app.config.midtrans.server-key}")
    private String midtransServerKey;

    @Bean(name = "midtrans")
    RestClient midtransRestClient() {
        String auth = Base64.getEncoder()
                .encodeToString((midtransServerKey + ":").getBytes());

        return RestClient.builder()
                .baseUrl(midtransBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
