package com.gymmick.smartgym.dto.trainer;

import com.gymmick.smartgym.entity.enums.PaymentType;
import com.gymmick.smartgym.validation.annotation.EnumValue;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class TrainerTransactionCreateRequest {

    @NotBlank(message = "The slot id field is required.")
    private String slotId;

    @NotBlank(message = "The payment type field is required.")
    @EnumValue(enumClass = PaymentType.class, message = "These provided payment type was invalid.")
    private String paymentType;

    public PaymentType getPaymentType() {
        return PaymentType.valueOf(this.paymentType.trim().toUpperCase());
    }
}
