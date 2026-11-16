package com.gymmick.smartgym.dto.transaction;

import java.util.*;

import com.gymmick.smartgym.entity.enums.TransactionType;

import lombok.*;

@Getter
@Setter
@Builder
public class TransactionData {

    private String packageId;

    private String orderId;

    private Long grossAmount;

    private TransactionType type;

    private List<Map<String, String>> itemDetails;
}
