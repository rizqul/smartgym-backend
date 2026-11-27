package com.gymmick.smartgym.dto.trainer;

import java.time.*;

import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class TrainerSlotUpdateRequest {

    private String description;

    @NotNull(message = "The slot date field is required.")
    @Date(message = "The slot date field must be a valid date.")
    @FutureOrPresentDate(message = "The slot date field must be in the future.")
    private String slotDate;

    @NotNull(message = "The start time field is required.")
    @Time(message = "The start time field must be a valid date time.")
    @FutureOrPresentDateTime(message = "The start time field must be in the future.")
    private String startTime;

    @NotNull(message = "The end time field is required.")
    @Time(message = "The end time field must be a valid date time.")
    @FutureOrPresentDateTime(message = "The end time field must be in the future.")
    private String endTime;

    public Instant getSlotDate() {
        return LocalDate.parse(this.slotDate).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public Instant getStartTime() {
        return LocalDate.parse(this.slotDate).atTime(LocalTime.parse(this.startTime))
                .toInstant(ZoneOffset.UTC).minus(Duration.ofHours(7));
    }

    public Instant getEndTime() {
        return LocalDate.parse(this.slotDate).atTime(LocalTime.parse(this.endTime))
                .toInstant(ZoneOffset.UTC).minus(Duration.ofHours(7));
    }
}