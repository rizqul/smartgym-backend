package com.gymmick.smartgym.services.trainer.impl;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.SlotStatus;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.TrainerMapping;
import com.gymmick.smartgym.mappers.TrainerSlotMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.trainer.TrainerService;
import com.gymmick.smartgym.services.utils.StorageService;
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

    @Value("${app.config.aws.s3.public-url}")
    private String awsPublicUrl;

    private final StorageService storageService;

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

        trainerRepository.save(trainer);
    }

    @Override
    @Transactional
    public void updateTrainer(String id, TrainerUpdateRequest request) {
        UUID trainerId = UUID.fromString(id);
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new ResourceNotFoundException());

        if (request.getName() != null && !request.getName().trim().isBlank()) {
            trainer.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isBlank()) {
            trainer.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isBlank()) {
            trainer.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getRate() != null) {
            trainer.setRate(request.getRate());
        }

        if (request.getExperience() != null) {
            trainer.setExperience(request.getExperience());
        }

        if (request.getSpecialization() != null) {
            trainer.setSpecialization(request.getSpecialization());
        }

        if (request.getPhoto() != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(trainer.getProfileUrl()).build();
            String fileKey = uriComponents.getPath().replaceFirst("/", "");

            if (fileKey.startsWith(String.format("trainer/%s", trainer.getId()))) {
                storageService.remove(fileKey);
            }

            String fileLocation = String.format("trainer/%s/profile", trainer.getId());
            fileKey = storageService.store((MultipartFile) request.getPhoto(), fileLocation);

            trainer.setProfileUrl(String.format("%s/%s", awsPublicUrl, fileKey));

        }

        trainerRepository.save(trainer);
    }

    @Override
    @Transactional
    public void createSlot(String id, TrainerSlotCreateRequest request) {

        Instant startTime = request.getStartTime();
        Instant endTime = request.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("The start time must be earlier than end time!");
        }

        UUID trainerId = UUID.fromString(id);
        boolean isOverlapping = trainerSlotRepository.existsOverlappingSlot(
                trainerId,
                startTime,
                endTime);

        if (isOverlapping) {
            throw new BadRequestException("The schedule clashes with another existing schedule for this trainer!");
        }

        Trainer trainer = this.findById(trainerId);
        System.out.println(trainer.getName());

        TrainerSlot trainerSlot = TrainerSlot.builder()
                .trainer(trainer)
                .description(request.getDescription())
                .slotDate(request.getSlotDate())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        trainerSlotRepository.save(trainerSlot);
    }

    @Override
    @Transactional
    public void updateSlot(String id, TrainerSlotUpdateRequest request) {

        UUID trainerSlotId = UUID.fromString(id);
        TrainerSlot trainerSlot = trainerSlotRepository.findById(trainerSlotId)
                .orElseThrow(() -> new ResourceNotFoundException());

        if (trainerSlot.getStatus().equals(SlotStatus.BOOKED)) {
            throw new BadRequestException("This slot cannot be updated because it has been booked!");
        }

        Instant startTime = request.getStartTime();
        Instant endTime = request.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("The start time must be earlier than end time!");
        }

        boolean isOverlapping = trainerSlotRepository.existsOverlappingSlotOnAnotherSlotId(
                trainerSlot.getId(),
                trainerSlot.getTrainer().getId(),
                startTime,
                endTime);

        if (isOverlapping) {
            throw new BadRequestException("The schedule clashes with another existing schedule for this trainer!");
        }

        trainerSlot.setDescription(request.getDescription());
        trainerSlot.setSlotDate(request.getSlotDate());
        trainerSlot.setStartTime(request.getStartTime());
        trainerSlot.setEndTime(request.getEndTime());

        trainerSlotRepository.save(trainerSlot);
    }

    public Trainer findById(UUID id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer data couldn't be found!"));
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

    @Override
    @Transactional
    public void deleteTrainerById(String id) {
        UUID trainerId = UUID.fromString(id);

        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new ResourceNotFoundException());
        trainerRepository.delete(trainer);
    }

}
