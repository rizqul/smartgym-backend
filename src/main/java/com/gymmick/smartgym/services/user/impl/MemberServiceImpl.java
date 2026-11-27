package com.gymmick.smartgym.services.user.impl;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.*;

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
import com.gymmick.smartgym.services.user.*;
import com.gymmick.smartgym.services.utils.*;
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
    private final StorageService storageService;

    private final UserMapping userMapping;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.config.aws.s3.public-url}")
    private String awsPublicUrl;

    @Override
    public long countAllMember() {
        return userRepository.countByRoleAndEnabledIsTrue(UserRole.MEMBER);
    }

    @Override
    public long countAllActiveMember() {
        return userRepository.countByRoleAndMembershipExpiredAtAfterAndEnabledIsTrue(UserRole.MEMBER, Instant.now());
    }

    @Override
    public long countAllInactiveMember() {
        return userRepository.countByRoleAndMembershipExpiredAtBeforeAndEnabledIsTrue(UserRole.MEMBER, Instant.now());
    }

    @Override
    public long countNewMember() {
        Instant now = Instant.now();
        Instant oneMonthAgo = ZonedDateTime.now()
                .minusMonths(1)
                .toInstant();

        return userRepository.countByRoleAndCreatedAtBetweenAndEnabledIsTrue(UserRole.MEMBER, oneMonthAgo, now);
    }

    @Override
    public PaginationResponse<UserResponse> findAllMember(UserSearchRequest request) {

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
    public void requestOtp(MemberOtpRequest request) throws EmailSenderException {

        String otpCode = OtpToken.generateOtp();

        otpService.store(request.getEmail(), otpCode, OtpType.EMAIL_VERIFICATION, Duration.ofMinutes(10));

        String emailTemplate = "email/auth/reset-password";
        String emailSubject = "Email Verification";

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
            throw new BadRequestException("These provided OTP code was invalid!");
        }

        otp.setExpiredAt(Instant.now());
        otpService.save(otp);

        String profileUrl = String
                .format("https://ui-avatars.com/api/?background=random&color=random&name=%s&size=256",
                        request.getName().toLowerCase().replaceAll("\s+", "+"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .profileUrl(profileUrl)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateMember(String id, MemberUpdateRequest request) {

        UUID memberId = UUID.fromString(id);
        User member = userService.findById(memberId);

        if (request.getName() != null && !request.getName().trim().isBlank()) {
            member.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isBlank()) {
            member.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isBlank()) {
            member.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isBlank()) {
            member.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getPhoto() != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(member.getProfileUrl()).build();
            String fileKey = uriComponents.getPath().replaceFirst("/", "");

            if (fileKey.startsWith(String.format("user/%s", member.getId()))) {
                storageService.remove(fileKey);
            }

            String fileLocation = String.format("user/%s/profile", member.getId());
            fileKey = storageService.store((MultipartFile) request.getPhoto(), fileLocation);

            member.setProfileUrl(String.format("%s/%s", awsPublicUrl, fileKey));
        }

        userRepository.save(member);
    }

    @Override
    @Transactional
    public void deleteMember(String id) {

        UUID memberId = UUID.fromString(id);
        User member = userService.findById(memberId);

        member.setMembershipExpiredAt(null);
        member.setDeletedAt(Instant.now());
        member.setEnabled(false);

        userRepository.save(member);
    }
}
