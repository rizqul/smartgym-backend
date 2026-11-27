package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.membership.MembershipPackageResponse;
import com.gymmick.smartgym.entity.MembershipPackage;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MembershipPackageMapping {

    List<MembershipPackageResponse> toListResponse(List<MembershipPackage> membershipPackages);
}
