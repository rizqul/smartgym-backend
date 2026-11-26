package com.gymmick.smartgym.controller.member;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.ai.AiWorkoutRequest;
import com.gymmick.smartgym.dto.ai.GroqAiResponse;
import com.gymmick.smartgym.entity.Equipment;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.ai.AiWorkOutPlanService;
import com.gymmick.smartgym.services.ai.GroqService;
import com.gymmick.smartgym.services.equipment.EquipmentService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/ai")
@RequiredArgsConstructor
public class GroqController {

    private final GroqService groqService;
    private final AiWorkOutPlanService aiWorkOutPlanService;
    private final EquipmentService equipmentService;

    @PostMapping(path = "/generate")
    public ResponseEntity<ApiResponse<GroqAiResponse>> generateWorkout(@AuthenticationPrincipal User member,
            @Valid @RequestBody AiWorkoutRequest request) {

        List<Equipment> equipments = equipmentService.getAllEquipment();

        String listEquipment = equipments.stream()
                .map(e -> "- %s (Status: %s)".formatted(e.getName(), e.getStatus()))
                .collect(Collectors.joining("\n"));

        String userRequest = """
                Here is my configuration:
                - Target: %s
                - Training Age: %s
                - Weekly Split: %s
                - Session Duration: %s
                - Focus Muscles: %s
                - Prioritize Available Machines: %b
                - Avoid Maintenance Equipment: %b

                Here is the Available Equipment List in my current gym branch today:
                %s

                Generate the plan.
                    """.formatted(
                request.getPrimaryTarget(),
                request.getTrainingAge(),
                request.getWeeklySplitInDays(),
                request.getSessionDuration(),
                request.getFocusAnatomy(),
                request.getPrioritizeMachines(),
                request.getAvoidMaintenance(),
                listEquipment);

        GroqAiResponse aiResponse = groqService.generateWorkout(userRequest);

        aiWorkOutPlanService.createWorkoutPlan(aiResponse, member);

        String message = "Berhasil generate workout plan!";

        return ApiResponse.success(HttpStatus.OK, message, aiResponse);
    }
}
