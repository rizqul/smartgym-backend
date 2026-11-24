package com.gymmick.smartgym.entity;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.gymmick.smartgym.entity.enums.TrainerSpecialization;
import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trainers")
@EntityListeners(AuditingEntityListener.class)
public class Trainer extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String profileUrl;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainerSpecialization specialization;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private Long rate;
}
