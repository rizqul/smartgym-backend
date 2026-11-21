package com.gymmick.smartgym.dto.transaction;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.dto.user.UserTransactionResponse;
import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Data
@Builder
public class TransactionResponse {

    private UUID id;

    private String orderId;

    private UUID packageId;

    private String name;

    private UserTransactionResponse member;

    private TransactionType type;

    private String paymentUrl;

    private PaymentType paymentType;

    private Long amount;

    private TransactionStatus status;

    private Instant paidAt;

    private Instant createdAt;

    private Instant updatedAt;
}
