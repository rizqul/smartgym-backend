package com.gymmick.smartgym.dto.ai;

import java.util.List;

import com.gymmick.smartgym.entity.WorkOutDay;
import com.gymmick.smartgym.entity.enums.SplitType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroqAiResponse {
  String title;
  String goal;
  List<WorkoutDayDto> schedule;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WorkoutDayDto {
    private String dayName;
    private SplitType splitType;
    private String focus;
    private Integer estimatedDurationMin;
    private List<ExerciseDto> exercises;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExerciseDto {
    private String name;
    private String equipment;
    private Integer sets;
    private String reps;
    private Integer restTimeSec;
    private String notes;
  }
}
