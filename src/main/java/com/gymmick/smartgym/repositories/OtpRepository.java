package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Otp;
import com.gymmick.smartgym.entity.enums.OtpType;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

    Optional<Otp> findByCodeAndType(String code, OtpType type);

    Optional<Otp> findByEmailAndCode(String email, String code);
}
