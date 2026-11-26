package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<Equipment>>> getAll() {

        List<Equipment> response = equipmentService.getAllEquipment();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ApiResponse<String>> createEquipment(@Valid @RequestBody EquipmentCreateRequest request) {

        equipmentService.createEquipment(request);

        String message = "Create enquipment successfully!";
        return ApiResponse.success(HttpStatus.OK, message);
    }
}
