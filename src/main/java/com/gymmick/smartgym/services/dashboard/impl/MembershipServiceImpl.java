package com.gymmick.smartgym.services.dashboard.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.MembershipPackageMapping;
import com.gymmick.smartgym.repositories.MembershipPackageRepository;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipServiceImpl implements MembershipService {

    private final MembershipPackageRepository membershipPackageRepository;
    private final TransactionService transactionService;

    private final MembershipPackageMapping membershipPackageMapping;

    @Override
    public List<MembershipPackageResponse> findAllPackage() {
        List<MembershipPackage> membershipPackages = membershipPackageRepository
                .findByDeletedAtIsNull(Sort.by(MembershipPackage::getPrice).ascending());

        return membershipPackageMapping.toListResponse(membershipPackages);
    }

    @Override
    @Transactional
    public void createPackage(MembershipPackageCreateRequest request) {
        MembershipPackage membershipPackage = MembershipPackage.builder()
                .name(request.getName())
                .duration(request.getDuration())
                .price(request.getPrice())
                .build();

        membershipPackageRepository.save(membershipPackage);
    }

    @Override
    @Transactional
    public void deletePackage(String id) {
        MembershipPackage membershipPackage = this.findById(id);

        membershipPackage.setDeletedAt(Instant.now());
        membershipPackageRepository.save(membershipPackage);
    }

    @Override
    @Transactional
    public String createMembership(MembershipTransactionCreateRequest request, User user) {
        try {
            MembershipPackage membershipPackage = this.findById(request.getPackageId());

            Map<String, String> itemDetail = new HashMap<>();
            itemDetail.put("id", request.getPackageId());
            itemDetail.put("name", membershipPackage.getName());
            itemDetail.put("price", membershipPackage.getPrice().toString());
            itemDetail.put("quantity", "1");

            List<Map<String, String>> itemDetails = new ArrayList<>();
            itemDetails.add(itemDetail);

            TransactionData transactionData = TransactionData.builder()
                    .packageId(request.getPackageId())
                    .orderId(request.getTransactionId())
                    .grossAmount(membershipPackage.getPrice())
                    .type(TransactionType.MEMBERSHIP)
                    .itemDetails(itemDetails)
                    .build();

            return transactionService.createTransaction(user, transactionData);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public MembershipPackage findById(String id) {
        UUID packageId = UUID.fromString(id);
        return membershipPackageRepository.findById(packageId)
                .orElseThrow(() -> new BadRequestException("Data Tidak Ditemukan!"));
    }
}
