package com.gymmick.smartgym.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.attendance.AttendanceFilterRequest;
import com.gymmick.smartgym.entity.Attendance;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.AttendingStatus;

import jakarta.persistence.criteria.Predicate;

public class AttendanceSpecification {

    public static Specification<Attendance> getMyAttendance(User member, AttendanceFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("member").get("id"), member.getId()));

            if (request.getStatus() != null && AttendingStatus.isValid(request.getStatus().trim().toUpperCase())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus().trim().toUpperCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
