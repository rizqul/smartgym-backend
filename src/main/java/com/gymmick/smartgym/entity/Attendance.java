package com.gymmick.smartgym.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendances")
@EntityListeners(AuditingEntityListener.class)
public class Attendance extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendingMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendingStatus status;

    @Column(nullable = false)
    private Instant checkInAt;

    @Column(nullable = true)
    private Instant checkOutAt;

    @Column(nullable = true)
    private String denialReason;

    @Column(nullable = true)
    private String deviceId;
}