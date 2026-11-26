package com.gymmick.smartgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.SplitType;
import com.gymmick.smartgym.entity.shared.TimeStamp;

@Entity
@Table(name = "workout_day")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOutDay extends TimeStamp {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String dayName;

  @Column(nullable = false)
  private Integer estimatedDurationMin;

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SplitType splitType = SplitType.FULL_BODY;

  @Column(nullable = false)
  private String focus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ai_workout_plan_id", nullable = false)
  private AiWorkoutPlan workoutPlan;

  @OneToMany(mappedBy = "workOutDay", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Exercise> exercises = new ArrayList<>();

  public void addExercise(Exercise exercise) {
    exercises.add(exercise);
    exercise.setWorkOutDay(this);
  }
}
