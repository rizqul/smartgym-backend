package com.gymmick.smartgym.services.auth.impl;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.Otp;
import com.gymmick.smartgym.entity.enums.OtpType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.repositories.OtpRepository;
import com.gymmick.smartgym.services.auth.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    @Override
    @Transactional
    public void store(String email, String code, OtpType type, Duration duration) {
        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .type(type)
                .expiredAt(Instant.now().plus(duration))
                .build();

        otpRepository.save(otp);
    };

    @Override
    public Otp findByEmailAndCode(String email, String code) {
        return otpRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new BadRequestException("Kode Otp Tidak Valid!"));
    };
}
