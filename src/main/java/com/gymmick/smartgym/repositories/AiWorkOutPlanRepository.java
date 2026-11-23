package com.gymmick.smartgym.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.AiWorkoutPlan;

public interface AiWorkOutPlanRepository extends JpaRepository<AiWorkoutPlan, UUID> {

}
