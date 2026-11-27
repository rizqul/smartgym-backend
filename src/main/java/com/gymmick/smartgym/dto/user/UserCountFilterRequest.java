package com.gymmick.smartgym.dto.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCountFilterRequest {

    String role;

    Instant startTime;

    Instant endTime;
}
