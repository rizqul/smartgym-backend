package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.entity.Attendance;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AttendanceMapping {

    AttendingResponse toResponse(Attendance attendance);

    List<AttendingResponse> toListResponses(List<Attendance> attendances);
}
