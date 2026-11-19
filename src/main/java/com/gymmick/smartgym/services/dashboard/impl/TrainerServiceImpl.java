package com.gymmick.smartgym.services.dashboard.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.dashboard.TrainerCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.dashboard.TrainerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerServiceImpl implements TrainerService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createTrainer(TrainerCreateRequest request) {

        String profileUrl = String
                .format("https://ui-avatars.com/api/?background=random&color=random&name=%s&size=256",
                        request.getName().toLowerCase().replaceAll("\s+", "+"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .profileUrl(profileUrl)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.TRAINER)
                .build();

        userService.save(user);
    }
}
