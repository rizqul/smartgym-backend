package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.gymmick.smartgym.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
