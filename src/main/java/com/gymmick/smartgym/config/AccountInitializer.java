package com.gymmick.smartgym.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.services.common.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountInitializer implements ApplicationRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.config.init-account}")
    private String[] account;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        String email = account[1].trim();

        if (!userService.existsByEmail(email)) {
            String emailHash = DigestUtils.sha256Hex(email);
            String profileUrl = String.format("https://gravatar.com/avatar/%s", emailHash);

            User user = User.builder()
                    .name(account[0])
                    .email(email)
                    .profileUrl(profileUrl)
                    .password(passwordEncoder.encode(account[2].trim()))
                    .role(UserRole.ADMIN)
                    .build();

            userService.store(user);
        }

    }
}