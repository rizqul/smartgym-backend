package com.gymmick.smartgym.mappers;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapping {

    UserResponse toResponse(User user);
}
