package com.gymmick.smartgym.services.ai;

import com.gymmick.smartgym.dto.ai.GroqAiResponse;
import com.gymmick.smartgym.entity.AiWorkoutPlan;
import com.gymmick.smartgym.entity.User;

public interface AiWorkOutPlanService {
  void createWorkoutPlan(GroqAiResponse aiResponse, User member);

  void deleteWorkoutPlan(String id);
}
