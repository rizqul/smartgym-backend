package com.gymmick.smartgym.dto.transaction;

import lombok.*;

@Getter
@Setter
@Builder
public class TransactionCreate {

    private String orderId;

    private String redirectUrl;

    private String token;
}
