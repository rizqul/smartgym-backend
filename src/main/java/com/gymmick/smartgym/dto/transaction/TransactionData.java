package com.gymmick.smartgym.dto.transaction;

import java.util.*;

import com.gymmick.smartgym.entity.enums.TransactionType;

import lombok.*;

@Getter
@Setter
@Builder
public class TransactionData {

    private UUID packageId;

    private Long grossAmount;

    private TransactionType type;

    private List<Map<String, String>> itemDetails;

    private String callbackUrl;
}
