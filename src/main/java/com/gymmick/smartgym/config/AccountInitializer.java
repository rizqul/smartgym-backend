package com.gymmick.smartgym.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.services.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountInitializer implements ApplicationRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.config.initial-account-name}")
    private String accountName;

    @Value("${app.config.initial-account-email}")
    private String accountEmail;

    @Value("${app.config.initial-account-password}")
    private String accountPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        String email = accountEmail.trim();

        if (!userService.existsByEmail(email)) {
            String profileUrl = String
                    .format("https://ui-avatars.com/api/?background=random&color=random&name=%s&size=256",
                            accountName.trim().toLowerCase().replaceAll("\\s+", ""));

            User user = User.builder()
                    .name(accountName)
                    .email(email)
                    .profileUrl(profileUrl)
                    .password(passwordEncoder.encode(accountPassword.trim()))
                    .role(UserRole.ADMIN)
                    .phoneNumber("081212341234")
                    .build();

            userService.save(user);
        }
    }
}