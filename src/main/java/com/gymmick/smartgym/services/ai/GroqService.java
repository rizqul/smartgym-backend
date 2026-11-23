package com.gymmick.smartgym.services.ai;

import com.gymmick.smartgym.dto.ai.GroqAiResponse;

public interface GroqService {

  GroqAiResponse generateWorkout(String request);
}
