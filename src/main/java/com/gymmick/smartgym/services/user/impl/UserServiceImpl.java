package com.gymmick.smartgym.services.user.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.*;

import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.services.utils.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.config.aws.s3.public-url}")
    private String awsPublicUrl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Incorrect email or password!"));
    }

    @Override
    @Transactional
    public User updateProfile(User user, UserProfileUpdateRequest request) {

        if (request.getName() != null && !request.getName().trim().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getPhoto() != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(user.getProfileUrl()).build();
            String fileKey = uriComponents.getPath().replaceFirst("/", "");

            if (fileKey.startsWith(String.format("user/%s", user.getId()))) {
                storageService.remove(fileKey);
            }

            String fileLocation = String.format("user/%s/profile", user.getId());
            fileKey = storageService.store((MultipartFile) request.getPhoto(), fileLocation);

            user.setProfileUrl(String.format("%s/%s", awsPublicUrl, fileKey));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updatePassword(User user, UserPasswordUpdateRequest request) {

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Password doesn't match!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findByIdAndEnabledIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
