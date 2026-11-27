package com.gymmick.smartgym;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.*;

@EnableAsync
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class SmartgymApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));

        SpringApplication.run(SmartgymApplication.class, args);
    }

}
