package com.gymmick.smartgym.entity;

import java.util.UUID;

import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise extends TimeStamp {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer sets;

  @Column(nullable = false)
  private String reps;

  @Column(nullable = false)
  private Float restTimeSec;

  @Column(columnDefinition = "TEXT", nullable = true)
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_day_id", nullable = false)
  private WorkOutDay workOutDay;

}
