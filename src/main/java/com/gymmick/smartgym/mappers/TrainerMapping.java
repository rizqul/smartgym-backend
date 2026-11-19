package com.gymmick.smartgym.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gymmick.smartgym.dto.user.UserTrainerResponse;
import com.gymmick.smartgym.entity.Trainer;
import com.gymmick.smartgym.entity.User;

@Component
public class TrainerMapping {

    public List<UserTrainerResponse> toListResponses(List<Trainer> trainers) {
        return trainers.stream()
                .map(trainer -> {
                    User user = trainer.getUser();

                    return UserTrainerResponse.builder()
                            .id(trainer.getId())
                            .userId(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .profileUrl(user.getProfileUrl())
                            .phoneNumber(user.getPhoneNumber())
                            .role(user.getRole())
                            .specialization(trainer.getSpecialization())
                            .experience(trainer.getExperience())
                            .rate(trainer.getRate())
                            .createdAt(trainer.getCreatedAt())
                            .updatedAt(trainer.getUpdatedAt())
                            .build();
                })
                .toList();
    }
}
