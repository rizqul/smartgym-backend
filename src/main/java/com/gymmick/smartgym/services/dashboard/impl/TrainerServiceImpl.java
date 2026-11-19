package com.gymmick.smartgym.services.dashboard.impl;

import java.time.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.common.TrainerSlotResponse;
import com.gymmick.smartgym.dto.common.UserTrainerResponse;
import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.mappers.TrainerMapping;
import com.gymmick.smartgym.mappers.TrainerSlotMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.dashboard.TrainerService;
import com.gymmick.smartgym.specification.TrainerSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerSlotRepository trainerSlotRepository;

    private final UserService userService;

    private final TrainerMapping trainerMapping;
    private final TrainerSlotMapping trainerSlotMapping;

    private final PasswordEncoder passwordEncoder;

    @Override
    public PaginationResponse<UserTrainerResponse> findAllTrainer(UserFilterRequest request) {
        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<Trainer> specification = TrainerSpecification.getTrainerSpecification(request);

        Sort sortBy = Sort.by(User::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<Trainer> trainerPage = trainerRepository.findAll(specification, pageRequest);
        List<UserTrainerResponse> trainers = trainerMapping.toListResponses(trainerPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, trainerPage.getTotalPages(), size,
                trainerPage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
                        .replaceQueryParam("page", p)
                        .toUriString());

        return PaginationResponse.<UserTrainerResponse>builder()
                .data(trainers)
                .pagination(paginationMeta)
                .build();
    }

    @Override
    public List<TrainerSlotResponse> findSlotByTrainerIdAndDate(String id, TrainerSlotFilterRequest request) {
        Instant targetDate = request.getDate();
        Instant endDate = targetDate.plus(Duration.ofDays(1));

        UUID trainerId = UUID.fromString(id);
        List<TrainerSlot> trainerSlots = trainerSlotRepository.findAllByTrainerIdAndSlotDateBetween(trainerId,
                targetDate, endDate);

        return trainerSlotMapping.toListResponse(trainerSlots);
    }

    @Override
    @Transactional
    public void createTrainer(TrainerCreateRequest request) {

        String profileUrl = String
                .format("https://ui-avatars.com/api/?background=random&color=random&name=%s&size=256",
                        request.getName().toLowerCase().replaceAll("\s+", "+"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .profileUrl(profileUrl)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.TRAINER)
                .build();

        User trainerUser = userService.save(user);

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .experience(request.getExperience())
                .specialization(request.getSpecialization())
                .rate(request.getRate())
                .build();

        this.save(trainer);
    }

    @Override
    @Transactional
    public void createSlot(String id, TrainerSlotRequest request) {

        Instant startTime = request.getStartTime();
        Instant endTime = request.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Waktu mulai harus lebih awal dari waktu selesai!");
        }

        UUID trainerId = UUID.fromString(id);
        Trainer trainer = this.findById(trainerId);

        boolean isOverlapping = trainerSlotRepository.existsOverlappingSlot(
                trainer.getId(),
                startTime,
                endTime);

        if (isOverlapping) {
            throw new BadRequestException("Jadwal bertabrakan dengan jadwal lain yang sudah ada untuk trainer ini!");
        }

        TrainerSlot trainerSlot = TrainerSlot.builder()
                .trainer(trainer)
                .description(request.getDescription())
                .quota(request.getQuota())
                .slotDate(request.getSlotDate())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        trainerSlotRepository.save(trainerSlot);
    }

    public Trainer findById(UUID id) {
        return trainerRepository.findById(id)
                .or(() -> trainerRepository.findByUserId(id))
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    public Trainer save(Trainer trainer) {
        return trainerRepository.save(trainer);
    }
}
