package com.gymmick.smartgym.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqConfig {

  @Value("${spring.ai.openai.chat.model}")
  private String model;

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
        .model(model)
        .temperature(0.4)
        .maxCompletionTokens(4096);

    return builder
        .defaultSystem(
            """
                You are an experienced and expert Personal Trainer for a gym.
                Create a workout plan based on the user's configuration and the available gym equipment.
                You MUST provide ONLY a valid JSON object that conforms to the specified TypeScript interface. Do not include any markdown formatting or additional text..

                            The JSON Schema must be:
                            {
                              "title": "String ,
                              "goal": "String",
                              "schedule": [
                                {
                                  "dayName": "String between [Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday]",
                                  "splitType": "String between [PULL, PUSH, UPPER, LOWER, FULL_BODY]"
                                  "focus": "String (e.g., Chest & Triceps)",
                                  "estimatedDurationMin": Number,
                                  "exercises": [
                                    {
                                      "name": "String",
                                      "equipment": "String",
                                      "sets": Number,
                                      "reps": "String",
                                      "restTimeSec": Number,
                                      "notes": "String"
                                    }
                                  ]
                                }
                              ],
                            }

                PROVISIONS:
                1. Use ONLY the equipment listed in the "Available Equipment List."
                2. If a piece of equipment is marked as "MAINTENANCE" (under repair) and the user has selected the "Avoid Maintenance Equipment" option, DO NOT use that equipment.
                3. Tailor the workout based on the user's anatomical focus and their level of exercise experience ("training age").
                                                            """)
        .defaultOptions(optionsBuilder)
        .build();
  }
}
