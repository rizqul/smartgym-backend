package com.gymmick.smartgym.services.user.impl;

import java.time.Instant;
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
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.user.*;
import com.gymmick.smartgym.services.utils.StorageService;
import com.gymmick.smartgym.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;

    private final UserService userService;
    private final StorageService storageService;

    private final UserMapping userMapping;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.config.aws.s3.public-url}")
    private String awsPublicUrl;

    @Override
    @Transactional
    public void createStaff(UserStaffCreateRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.STAFF)
                .build();

        userService.save(user);
    }

    @Override
    @Transactional
    public void deleteStaff(String id) {

        UUID userId = UUID.fromString(id);
        User user = userService.findById(userId);

        user.setDeletedAt(Instant.now());
        userService.save(user);
    }

    @Override
    public PaginationResponse<UserResponse> findAllStaff(UserFilterRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<User> specification = UserSpecification.getUserByRoleSpecification(request, UserRole.STAFF);

        Sort sortBy = Sort.by(User::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<User> userPage = userRepository.findAll(specification, pageRequest);

        List<UserResponse> userResponse = userMapping.toListResponses(userPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, userPage.getTotalPages(), size,
                userPage.getTotalElements(),
                p -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", p).toUriString());

        PaginationResponse<UserResponse> paginationResponse = PaginationResponse.<UserResponse>builder()
                .data(userResponse)
                .pagination(paginationMeta)
                .build();

        return paginationResponse;
    }

    @Override
    @Transactional
    public void updateStaff(String id, UserStaffUpdateRequest request) {

        UUID staffId = UUID.fromString(id);
        User staff = userRepository.findById(staffId).orElseThrow(() -> new ResourceNotFoundException());

        if (request.getPhoto() != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(staff.getProfileUrl()).build();
            String fileKey = uriComponents.getPath().replaceFirst("/", "");

            if (fileKey.startsWith(String.format("staff/%s", staff.getId()))) {
                storageService.remove(fileKey);
            }

            String fileLocation = String.format("staff/%s/profile", staff.getId());
            fileKey = storageService.store((MultipartFile) request.getPhoto(), fileLocation);

            staff.setProfileUrl(String.format("%s/%s", awsPublicUrl, fileKey));
        }

        String name = (request.getName() != null && !request.getName().isBlank()) ? request.getName() : staff.getName();

        String email = (request.getEmail() != null && !request.getEmail().isBlank()) ? request.getEmail()
                : staff.getEmail();

        String phoneNumber = (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank())
                ? request.getPhoneNumber()
                : staff.getPhoneNumber();

        String password = (request.getPassword() != null && !request.getPassword().isBlank()) ? request.getPassword()
                : staff.getPassword();

        if (!Objects.equals(request.getEmail(), staff.getEmail())) {
            staff.setEmail(email);
        }

        staff.setName(name);
        staff.setPhoneNumber(phoneNumber);
        staff.setPassword(password);

        userRepository.save(staff);
    }
}