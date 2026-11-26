package com.gymmick.smartgym.controller.landing;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.membership.MembershipPackageResponse;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.trainer.TrainerService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/landing")
@RequiredArgsConstructor
public class LandingPageController {

    private final MembershipService membershipService;
    private final TrainerService trainerService;

    @GetMapping(path = "/membership-packages")
    public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllMemberPackage() {

        Map<MembershipType, List<MembershipPackageResponse>> response = membershipService.findAllPackage();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @GetMapping(path = "/trainer")
    public ResponseEntity<ApiResponse<List<TrainerResponse>>> findAll() {

        List<TrainerResponse> response = trainerService.findAllTrainer();

        return ApiResponse.success(HttpStatus.OK, response);
    }
}
