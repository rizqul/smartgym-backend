package com.gymmick.smartgym.dto.common;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.entity.enums.TransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {

  private UUID id;

  private UUID packageId;

  private String orderId;

  private String paymentUrl;

  private User member;

  private TransactionType type;

  private Long amount;

  private TransactionStatus status;

  private Instant paidAt;

  private Instant createdAt;

  private Instant updatedAt;
}
