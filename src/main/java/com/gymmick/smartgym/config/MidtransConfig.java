package com.gymmick.smartgym.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.midtrans.Config;
import com.midtrans.Midtrans;
import com.midtrans.service.MidtransSnapApi;
import com.midtrans.service.impl.MidtransSnapApiImpl;

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
    Config snapConfig() {
        return Config.builder()
                .setPaymentOverrideNotification(notificationUrl)
                .build();
    }

    @Bean
    MidtransSnapApi midtransSnapApi(Config snapConfig) {
        return new MidtransSnapApiImpl(snapConfig);
    }
}
