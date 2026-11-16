package com.gymmick.smartgym.services.dashboard;

import java.util.List;

import com.gymmick.smartgym.dto.dashboard.MembershipPackageCreateRequest;
import com.gymmick.smartgym.dto.dashboard.MembershipPackageResponse;
import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;

public interface MembershipService {

    List<MembershipPackageResponse> findAllPackage();

    void createPackage(MembershipPackageCreateRequest request);

    void deletePackage(String id);

    String createMembership(MembershipTransactionCreateRequest request, User user);
}
