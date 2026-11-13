package com.gymmick.smartgym.services.common.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Cacheable(value = "user", key = "#id", condition = "#id != null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("These credentials do not match our records."));
    }

    @Override
    @Transactional
    public void store(User user) {
        userRepository.save(user);
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
}
