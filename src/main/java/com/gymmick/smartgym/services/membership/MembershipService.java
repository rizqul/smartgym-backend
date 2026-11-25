package com.gymmick.smartgym.services.membership;

import java.util.*;

import com.gymmick.smartgym.dto.membership.*;
import com.gymmick.smartgym.dto.transaction.TransactionCreate;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.MembershipType;

public interface MembershipService {

    Map<MembershipType, List<MembershipPackageResponse>> findAllPackage();

    void createPackage(MembershipPackageCreateRequest request);

    void updatePackage(String id, MembershipPackageUpdateRequest request);

    void deletePackage(String id);

    TransactionCreate buyMembership(User user, MembershipTransactionCreateRequest request);

    void activationMembership(Transaction transaction);

    MembershipPackage findById(String id);

    Long countMembershipPackage();
}
