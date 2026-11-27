package com.gymmick.smartgym.services.equipment.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.equipment.EquipmentCreateRequest;
import com.gymmick.smartgym.entity.Equipment;
import com.gymmick.smartgym.repositories.EquipmentRepository;
import com.gymmick.smartgym.services.equipment.EquipmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional
    public void createEquipment(EquipmentCreateRequest request) {
        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .status(request.getEquipmentStatus())
                .build();

        equipmentRepository.save(equipment);
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }
}
