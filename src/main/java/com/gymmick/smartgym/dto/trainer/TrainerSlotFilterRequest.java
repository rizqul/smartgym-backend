package com.gymmick.smartgym.dto.trainer;

import java.time.*;
import lombok.*;

@Getter
@Setter
public class TrainerSlotFilterRequest {

    private String date;

    public Instant getDate() {
        LocalDate localDate;

        if (this.date != null && this.date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
            localDate = LocalDate.parse(this.date);
        } else {
            localDate = LocalDate.now(ZoneId.systemDefault());
        }

        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}