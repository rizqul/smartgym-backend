package com.gymmick.smartgym.dto.membership;

import java.util.UUID;

import com.gymmick.smartgym.entity.enums.MembershipType;

import lombok.*;

@Getter
@Setter
@Builder
public class MembershipPackageResponse {

    private UUID id;

    private String name;

    private String description;

    private MembershipType type;

    private Integer duration;

    private Long price;
}
