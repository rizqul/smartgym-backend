package com.gymmick.smartgym.services.dashboard;

import java.util.*;

import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.MembershipType;

public interface MembershipService {

    Map<MembershipType, List<MembershipPackageResponse>> findAllPackage();

    void createPackage(MembershipPackageCreateRequest request);

    void deletePackage(String id);

    String buyMembership(MembershipTransactionCreateRequest request, User user);

    void activationMembership(String orderId);
}
