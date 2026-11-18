package com.gymmick.smartgym.services.auth;

import java.util.UUID;

import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TokenType;

public interface TokenService {

    void store(UUID id, User user, TokenType type);

    void store(UUID id, UUID refreshId, User user, TokenType type);

    void save(Token token);

    Token findById(UUID id);

    Token findByRefreshId(UUID id);
}
