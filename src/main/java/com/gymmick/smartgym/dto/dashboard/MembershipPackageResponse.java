package com.gymmick.smartgym.dto.dashboard;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
public class MembershipPackageResponse {

    private UUID id;

    private String name;

    private Integer duration;

    private Long price;
}
