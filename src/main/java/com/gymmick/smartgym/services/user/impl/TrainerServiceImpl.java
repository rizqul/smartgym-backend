package com.gymmick.smartgym.services.user.impl;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.TrainerMapping;
import com.gymmick.smartgym.mappers.TrainerSlotMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.user.*;
import com.gymmick.smartgym.specification.TrainerSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerSlotRepository trainerSlotRepository;

    private final TrainerMapping trainerMapping;
    private final TrainerSlotMapping trainerSlotMapping;

    @Override
    public PaginationResponse<TrainerResponse> findAllTrainer(TrainerSearchRequest request) {
        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<Trainer> specification = TrainerSpecification.getTrainerSpecification(request);

        Sort sortBy = Sort.by(Trainer::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<Trainer> trainerPage = trainerRepository.findAll(specification, pageRequest);
        List<TrainerResponse> trainers = trainerMapping.toListResponses(trainerPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, trainerPage.getTotalPages(), size,
                trainerPage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
                        .replaceQueryParam("page", p)
                        .toUriString());

        return PaginationResponse.<TrainerResponse>builder()
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

        Trainer trainer = Trainer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .profileUrl(profileUrl)
                .phoneNumber(request.getPhoneNumber())
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

        Instant slotDate = request.getSlotDate().plus(Duration.ofHours(7));

        if (!slotDate.equals(startTime.truncatedTo(ChronoUnit.DAYS))
                && !slotDate.equals(endTime.truncatedTo(ChronoUnit.DAYS))) {

            throw new BadRequestException("Tanggal jadwal harus sama dengan waktu training!");
        }

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Waktu mulai harus lebih awal dari waktu selesai!");
        }

        UUID trainerId = UUID.fromString(id);
        boolean isOverlapping = trainerSlotRepository.existsOverlappingSlot(
                trainerId,
                startTime,
                endTime);

        if (isOverlapping) {
            throw new BadRequestException("Jadwal bertabrakan dengan jadwal lain yang sudah ada untuk trainer ini!");
        }

        Trainer trainer = this.findById(trainerId);
        TrainerSlot trainerSlot = TrainerSlot.builder()
                .trainer(trainer)
                .description(request.getDescription())
                .slotDate(request.getSlotDate())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        trainerSlotRepository.save(trainerSlot);
    }

    public Trainer findById(UUID id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    public Trainer save(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Override
    public Long countAllTrainer() {
        return trainerRepository.count();
    }

    @Override
    public Long countAllTrainerThatHasSlotDaily(Instant startOfDay, Instant endOfDay) {
        return trainerSlotRepository.countDistinctTrainersWithSlotsOnDate(startOfDay, endOfDay);
    }

    @Override
    public Long countAllSlotDaily(Instant startOfDay, Instant endOfDay) {
        return trainerSlotRepository.countTrainerSlotOnDate(startOfDay, endOfDay);
    }
}
