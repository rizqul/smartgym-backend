package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Token;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findById(UUID id);

    Optional<Token> findByRefreshId(UUID refreshId);

}
