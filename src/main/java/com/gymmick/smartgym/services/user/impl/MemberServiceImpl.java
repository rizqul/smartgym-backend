package com.gymmick.smartgym.services.user.impl;

import java.time.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.member.*;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.auth.OtpService;
import com.gymmick.smartgym.services.user.MemberService;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.services.utils.EmailService;
import com.gymmick.smartgym.specification.UserSpecification;
import com.gymmick.smartgym.utils.OtpToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final UserRepository userRepository;

    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;

    private final UserMapping userMapping;

    private final PasswordEncoder passwordEncoder;

    @Override
    public PaginationResponse<UserResponse> findAllMember(UserFilterRequest request) {
        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<User> specification = UserSpecification.getUserByRoleSpecification(request, UserRole.MEMBER);

        Sort sortBy = Sort.by(User::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<User> userPage = userRepository.findAll(specification, pageRequest);
        List<UserResponse> userResponse = userMapping.toListResponses(userPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, userPage.getTotalPages(), size,
                userPage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
                        .replaceQueryParam("page", p)
                        .toUriString());

        return PaginationResponse.<UserResponse>builder()
                .data(userResponse)
                .pagination(paginationMeta)
                .build();
    }

    @Override
    @Transactional
    public void requestOtp(MemberOtpRequest request)
            throws EmailSenderException {

        String otpCode = OtpToken.generateOtp();

        otpService.store(request.getEmail(), otpCode, OtpType.EMAIL_VERIFICATION, Duration.ofMinutes(10));

        String emailTemplate = "email/auth/reset-password";
        String emailSubject = "Verifikasi Email";

        Map<String, Object> variables = new HashMap<>();
        variables.put("email", request.getEmail());
        variables.put("code", otpCode);

        emailService.sendHtmlEmail(emailTemplate, emailSubject, request.getEmail(), variables);
    }

    @Override
    @Transactional
    public void createMember(MemberCreateRequest request) {

        Otp otp = otpService.findByEmailAndCode(request.getEmail(), request.getCode());

        if (!otp.getType().equals(OtpType.EMAIL_VERIFICATION) || otp.getExpiredAt().isBefore(Instant.now())) {
            throw new BadRequestException("Kode Otp Tidak Valid!");
        }

        otp.setExpiredAt(Instant.now());
        otpService.save(otp);

        String profileUrl = String
                .format("https://ui-avatars.com/api/?background=random&color=random&name=%s&size=256",
                        request.getName().toLowerCase().replaceAll("\s+", "+"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .profileUrl(profileUrl)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userService.save(user);
    }

    @Override
    @Transactional
    public void deleteMember(String id) {

        UUID userId = UUID.fromString(id);
        User user = userService.findById(userId);

        user.setMembershipExpiredAt(null);
        user.setDeletedAt(Instant.now());

        userService.save(user);
    }

    @Override
    public long countAllActiveMember() {

        return userRepository.countByRoleAndMembershipExpiredAtAfter(UserRole.MEMBER, Instant.now());
    }

    @Override
    public long countAllInactiveMember() {

        return userRepository.countByRoleAndMembershipExpiredAtBefore(UserRole.MEMBER, Instant.now());
    }

    public long countNewMemberAccount() {

        Instant now = Instant.now();
        Instant oneMonthAgoLocal = ZonedDateTime.now()
                .minusMonths(1)
                .toInstant();

        return userRepository.countByRoleAndCreatedAtBetween(UserRole.MEMBER, now, oneMonthAgoLocal);
    }

    @Override
    public long countAllMember() {

        return userRepository.countByRole(UserRole.MEMBER);
    }

    @Override
    public long countNewMember() {

        Instant now = Instant.now();
        Instant oneMonthAgoLocal = ZonedDateTime.now()
                .minusMonths(1)
                .toInstant();

        return userRepository.countByRoleAndCreatedAtBetween(UserRole.MEMBER, now, oneMonthAgoLocal);
    }
}
