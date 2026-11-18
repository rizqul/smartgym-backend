package com.gymmick.smartgym.services.dashboard.impl;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.MembershipPackageMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipPackageRepository membershipPackageRepository;

    private final TransactionRespository transactionRespository;
    private final UserRepository userRepository;

    private final TransactionService transactionService;

    private final MembershipPackageMapping membershipPackageMapping;

    @Value("${app.config.midtrans.membership.redirect-url}")
    private String membershipRedirectUrl;

    @Override
    public Map<MembershipType, List<MembershipPackageResponse>> findAllPackage() {
        List<MembershipPackage> membershipPackages = membershipPackageRepository
                .findByDeletedAtIsNull(Sort.by(MembershipPackage::getPrice).ascending());

        return membershipPackageMapping.toListResponse(membershipPackages).stream()
                .collect(Collectors.groupingBy(MembershipPackageResponse::getType));
    }

    @Override
    @Transactional
    public void createPackage(MembershipPackageCreateRequest request) {
        MembershipPackage membershipPackage = MembershipPackage.builder()
                .type(request.getType())
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .price(request.getPrice())
                .build();

        membershipPackageRepository.save(membershipPackage);
    }

    @Override
    @Transactional
    public void updatePackage(String id, MembershipPackageUpdateRequest request) {
        MembershipPackage membershipPackage = this.findById(id);

        membershipPackage.setType(request.getType());
        membershipPackage.setName(request.getName());
        membershipPackage.setDescription(request.getDescription());
        membershipPackage.setDuration(request.getDuration());

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
    public String buyMembership(MembershipTransactionCreateRequest request, User user) {
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
                    .packageId(UUID.fromString(request.getPackageId()))
                    .orderId(request.getTransactionId())
                    .grossAmount(membershipPackage.getPrice())
                    .type(TransactionType.MEMBERSHIP)
                    .itemDetails(itemDetails)
                    .callbackUrl(membershipRedirectUrl)
                    .build();

            return transactionService.createTransaction(user, transactionData);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void activationMembership(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.PROCESS) {
            MembershipPackage membershipPackage = this.findById(transaction.getPackageId().toString());

            Membership membership = Membership.builder()
                    .transaction(transaction)
                    .membershipPackage(membershipPackage)
                    .member(transaction.getMember())
                    .startDate(Instant.now())
                    .endDate(Instant.now().plus(Duration.ofDays(membershipPackage.getDuration())))
                    .build();

            membershipRepository.save(membership);

            User user = transaction.getMember();
            user.setMembershipStatus(MembershipStatus.ACTIVE);
            user.setMembershipExpiredAt(
                    user.getMembershipExpiredAt() == null
                            ? Instant.now().plus(Duration.ofDays(membershipPackage.getDuration()))
                            : user.getMembershipExpiredAt().plus(Duration.ofDays(membershipPackage.getDuration())));

            userRepository.save(user);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRespository.save(transaction);
        }
    };

    @Override
    public MembershipPackage findById(String id) {
        UUID packageId = UUID.fromString(id);
        return membershipPackageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException());
    }
}
