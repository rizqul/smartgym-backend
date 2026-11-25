package com.gymmick.smartgym.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

}
