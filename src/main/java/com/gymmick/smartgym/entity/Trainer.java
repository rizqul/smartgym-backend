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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private TrainerSpecialization specialization;

    private Integer experience;

    @Column(nullable = false)
    private Long rate;
}
