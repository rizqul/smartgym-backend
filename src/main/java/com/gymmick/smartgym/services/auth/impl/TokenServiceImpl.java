package com.gymmick.smartgym.services.auth.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TokenType;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.repositories.TokenRepository;
import com.gymmick.smartgym.services.auth.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public void store(UUID id, User user, TokenType type) {
        Token token = Token.builder()
                .id(id)
                .user(user)
                .type(type)
                .build();

        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void store(UUID id, UUID refreshId, User user, TokenType type) {
        Token token = Token.builder()
                .id(id)
                .refreshId(refreshId)
                .user(user)
                .type(type)
                .build();

        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void save(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public Token findById(UUID id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new AuthenticationDeniedException());
    }

    @Override
    public Token findByRefreshId(UUID id) {
        return tokenRepository.findByRefreshId(id)
                .orElseThrow(() -> new BadRequestException("These JWT token was invalid."));
    }
}
