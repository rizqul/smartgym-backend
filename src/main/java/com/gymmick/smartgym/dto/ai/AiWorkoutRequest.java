package com.gymmick.smartgym.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiWorkoutRequest {

  @NotBlank(message = "primary target cannot be blank")
  private String primaryTarget;

  @NotBlank(message = "training target cannot be blank")
  private String trainingAge;

  @NotNull
  private Integer weeklySplitInDays;

  @NotBlank(message = "session duration cannot be blank")
  private String sessionDuration;

  @NotNull
  private String[] focusAnatomy;

  private Boolean prioritizeMachines = true;

  private Boolean avoidMaintenance = true;
}
