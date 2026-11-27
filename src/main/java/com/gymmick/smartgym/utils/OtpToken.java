package com.gymmick.smartgym.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class OtpToken {

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();

        return random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
