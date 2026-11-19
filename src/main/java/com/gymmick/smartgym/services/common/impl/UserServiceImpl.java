package com.gymmick.smartgym.services.common.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.*;

import com.gymmick.smartgym.dto.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.storage.StorageService;

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
    @Cacheable(value = "user", key = "#id", condition = "#id != null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Alamat Email atau Kata Sandi Salah!"));
    }

    @Override
    @Transactional
    public User updateProfile(User user, UserProfileUpdateRequest request) {

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

        String name = (request.getName() != null && !request.getName().isBlank()) ? request.getName() : user.getName();

        String phoneNumber = (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank())
                ? request.getPhoneNumber()
                : user.getPhoneNumber();

        user.setName(name);
        user.setPhoneNumber(phoneNumber);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updatePassword(User user, UserPasswordUpdateRequest request) {

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Kata sandi tidak sesuai!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
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
