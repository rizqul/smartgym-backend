package com.gymmick.smartgym.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.gymmick.smartgym.entity.enums.SlotStatus;
import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trainer_slots")
@EntityListeners(AuditingEntityListener.class)
public class TrainerSlot extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    private String description;

    @Column(nullable = false)
    private Instant slotDate;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.AVAILABLE;
}
