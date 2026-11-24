package com.gymmick.smartgym.entity;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.gymmick.smartgym.entity.enums.EquipmentStatus;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment")
@EntityListeners(AuditingEntityListener.class)
public class Equipment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Builder.Default
  private EquipmentStatus status = EquipmentStatus.AVAILABLE;

  @Column(nullable = true)
  private String profileUrl;
}
