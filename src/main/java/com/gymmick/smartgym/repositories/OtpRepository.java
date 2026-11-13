package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

    Optional<Otp> findByEmailAndCode(String email, String code);
}
