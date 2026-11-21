package com.gymmick.smartgym.dto.membership;

import com.gymmick.smartgym.entity.enums.PaymentType;
import com.gymmick.smartgym.validation.annotation.EnumValue;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class MembershipTransactionCreateRequest {

    @NotBlank(message = "The package id field is required.")
    private String packageId;

    @NotBlank(message = "The payment type field is required.")
    @EnumValue(enumClass = PaymentType.class, message = "These provided payment type was invalid.")
    private String paymentType;

    public PaymentType getPaymentType() {
        return PaymentType.valueOf(this.paymentType.trim().toUpperCase());
    }
}
