package com.gymmick.smartgym.services.dashboard.impl;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.dashboard.MemberService;
import com.gymmick.smartgym.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final UserRepository userRepository;

    private final UserMapping userMapping;

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

        PaginationResponse<UserResponse> paginationResponse = PaginationResponse.<UserResponse>builder()
                .data(userResponse)
                .pagination(paginationMeta)
                .build();

        return paginationResponse;
    }
}
