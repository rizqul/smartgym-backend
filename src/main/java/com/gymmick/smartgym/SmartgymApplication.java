package com.gymmick.smartgym;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PostConstruct;

@EnableAsync
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class SmartgymApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartgymApplication.class, args);
    }

}
