package com.gymmick.smartgym.services.equipment;

import java.util.List;

import com.gymmick.smartgym.dto.equipment.EquipmentCreateRequest;
import com.gymmick.smartgym.entity.Equipment;

public interface EquipmentService {

  void createEquipment(EquipmentCreateRequest request);

  List<Equipment> getAllEquipment();

}
