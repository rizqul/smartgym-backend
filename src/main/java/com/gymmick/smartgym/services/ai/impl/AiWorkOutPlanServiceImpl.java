package com.gymmick.smartgym.services.ai.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.ai.GroqAiResponse;
import com.gymmick.smartgym.entity.AiWorkoutPlan;
import com.gymmick.smartgym.entity.Exercise;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.WorkOutDay;
import com.gymmick.smartgym.repositories.AiWorkOutPlanRepository;
import com.gymmick.smartgym.services.ai.AiWorkOutPlanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiWorkOutPlanServiceImpl implements AiWorkOutPlanService {

  private final AiWorkOutPlanRepository aiWorkOutPlanRepository;

  @Override
  @Transactional
  public void createWorkoutPlan(GroqAiResponse aiResponse, User member) {

    AiWorkoutPlan workoutPlan = AiWorkoutPlan.builder()
        .title(aiResponse.getTitle())
        .goal(aiResponse.getGoal())
        .user(member)
        .build();

    List<WorkOutDay> workoutDays = aiResponse.getSchedule()
        .stream()
        .map(dayDto -> {

          WorkOutDay workoutDay = WorkOutDay.builder()
              .dayName(dayDto.getDayName())
              .splitType(dayDto.getSplitType())
              .focus(dayDto.getFocus())
              .estimatedDurationMin(dayDto.getEstimatedDurationMin())
              .build();

          List<Exercise> exercises = dayDto.getExercises()
              .stream()
              .map(exerciseDto -> Exercise.builder()
                  .name(exerciseDto.getName())
                  .sets(exerciseDto.getSets())
                  .reps(exerciseDto.getReps())
                  .restTimeSec(exerciseDto.getRestTimeSec().floatValue())
                  .notes(exerciseDto.getNotes())
                  .build())
              .toList();

          exercises.forEach(workoutDay::addExercise);

          return workoutDay;

        })
        .toList();

    workoutDays.forEach(workoutPlan::addWorkoutDay);

    aiWorkOutPlanRepository.save(workoutPlan);
  }

  @Override
  public void deleteWorkoutPlan(String id) {
    aiWorkOutPlanRepository.findById(UUID.fromString(id));
  }

}
