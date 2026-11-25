package com.gymmick.smartgym.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.midtrans.*;
import com.midtrans.service.*;
import com.midtrans.service.impl.*;

import jakarta.annotation.PostConstruct;

@Configuration
public class MidtransConfig {

    @Value("${app.config.midtrans.server-key}")
    private String serverKey;

    @Value("${app.config.midtrans.client-key}")
    private String clientKey;

    @Value("${app.config.midtrans.production}")
    private Boolean production;

    @Value("${app.config.midtrans.notification-url}")
    private String notificationUrl;

    @PostConstruct
    public void midtransConfig() {
        Midtrans.serverKey = serverKey;
        Midtrans.clientKey = clientKey;
        Midtrans.isProduction = production;
    }

    @Bean
    Config midtransApiConfig() {
        return Config.builder()
                .setPaymentOverrideNotification(notificationUrl)
                .build();
    }

    @Bean
    MidtransCoreApi midtransCoreApi(Config midtransApiConfig) {
        return new MidtransCoreApiImpl(midtransApiConfig);
    }

    @Bean
    MidtransSnapApi midtransSnapApi(Config midtransApiConfig) {
        return new MidtransSnapApiImpl(midtransApiConfig);
    }
}
