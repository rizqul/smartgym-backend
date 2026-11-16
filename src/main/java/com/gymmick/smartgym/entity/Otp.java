package com.gymmick.smartgym.entity;

import java.time.Instant;
import java.util.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.gymmick.smartgym.entity.enums.OtpType;
import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otps")
@EntityListeners(AuditingEntityListener.class)
public class Otp extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    @Column(nullable = false)
    private Instant expiredAt;

}
