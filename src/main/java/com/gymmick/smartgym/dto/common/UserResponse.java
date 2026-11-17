package com.gymmick.smartgym.dto.common;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.MembershipStatus;
import com.gymmick.smartgym.entity.enums.UserRole;

import lombok.*;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String name;

    private String email;

    private String profileUrl;

    private UserRole role;

    private String phoneNumber;

    private MembershipStatus membershipStatus;

    private Instant membershipExpiredAt;

    private Instant createdAt;

    private Instant updatedAt;
}
