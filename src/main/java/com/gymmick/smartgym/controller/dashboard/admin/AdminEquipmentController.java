package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.equipment.EquipmentCreateRequest;
import com.gymmick.smartgym.entity.Equipment;
import com.gymmick.smartgym.services.equipment.EquipmentService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/equipments")
@RequiredArgsConstructor
public class AdminEquipmentController {

  private final EquipmentService equipmentService;

  @PostMapping(path = "/create")
  public ResponseEntity<ApiResponse<String>> createEquipment(@Valid @RequestBody EquipmentCreateRequest request) {

    equipmentService.createEquipment(request);

    String message = "successfully created equipment";
    return ApiResponse.success(HttpStatus.OK, message);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<Equipment>>> getAll() {

    List<Equipment> response = equipmentService.getAllEquipment();

    String message = "successfully get equipment";
    return ApiResponse.success(HttpStatus.OK, message, response);
  }
}
