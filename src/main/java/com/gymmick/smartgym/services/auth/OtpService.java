package com.gymmick.smartgym.services.auth;

import java.time.Duration;

import com.gymmick.smartgym.entity.Otp;
import com.gymmick.smartgym.entity.enums.OtpType;

public interface OtpService {

    void store(String email, String code, OtpType type, Duration duration);

    Otp findByEmailAndCode(String email, String code);
}
