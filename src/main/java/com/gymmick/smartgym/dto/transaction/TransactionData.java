package com.gymmick.smartgym.dto.transaction;

import java.util.*;

import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Getter
@Setter
@Builder
public class TransactionData {

    private UUID packageId;

    private String packageName;

    private Long grossAmount;

    private PaymentType paymentType;

    private TransactionType type;

    private List<Map<String, String>> itemDetails;

    private String callbackUrl;
}
