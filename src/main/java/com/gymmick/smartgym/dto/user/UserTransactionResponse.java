package com.gymmick.smartgym.dto.user;

import java.util.UUID;

import lombok.*;

@Data
@Builder
public class UserTransactionResponse {

    private UUID id;

    private String name;

    private String email;

    private String profileUrl;

    private String phoneNumber;
}
