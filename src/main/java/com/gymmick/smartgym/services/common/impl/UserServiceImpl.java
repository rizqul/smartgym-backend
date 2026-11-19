package com.gymmick.smartgym.services.common.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.UserUpdatePasswordRequest;
import com.gymmick.smartgym.dto.UserUpdateProfileRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.common.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = "user", key = "#id", condition = "#id != null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Alamat Email atau Kata Sandi Salah!"));
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

    @Override
    @Transactional
    public User updateProfile(UUID id, UserUpdateProfileRequest request) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());

        String name = (request.getName() != null && !request.getName().isBlank()) ? request.getName() : user.getName();

        String phoneNumber = (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank())
                ? request.getPhoneNumber()
                : user.getPhoneNumber();

        String profileUrl = (request.getProfileUrl() != null && !request.getProfileUrl().isBlank())
                ? request.getProfileUrl()
                : user.getProfileUrl();

        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setProfileUrl(profileUrl);

        User updatedUser = userRepository.save(user);
        return updatedUser;
    }

    @Override
    @Transactional
    public User updatePassword(String email, UserUpdatePasswordRequest request) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("incorrect password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User updatedPasswordUser = userRepository.save(user);
        return updatedPasswordUser;
    }
}
