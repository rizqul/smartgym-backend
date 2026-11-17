package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.MembershipPackage;

public interface MembershipPackageRepository extends JpaRepository<MembershipPackage, UUID> {

    List<MembershipPackage> findByDeletedAtIsNull(Sort sort);

    Optional<MembershipPackage> findByIdAndDeletedAtIsNull(UUID id);
}
