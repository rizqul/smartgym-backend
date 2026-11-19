package com.gymmick.smartgym.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class MembershipTransactionCreateRequest {

    @NotBlank(message = "The package id field is required.")
    private String packageId;
}
