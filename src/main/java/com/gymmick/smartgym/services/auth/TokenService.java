package com.gymmick.smartgym.services.auth;

import java.util.UUID;

import com.gymmick.smartgym.entity.*;

public interface TokenService {

    void store(UUID id, UUID refreshId, User user);

    void update(Token token);

    Token findById(UUID id);

    Token findByRefreshId(UUID id);
}
