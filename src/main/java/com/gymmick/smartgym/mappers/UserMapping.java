package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapping {

    UserResponse toResponse(User user);

    List<UserResponse> toListResponses(List<User> users);
}
