package com.gymmick.smartgym.dto.attendance;

import lombok.*;

@Getter
@Setter
public class AttendanceFilterRequest {

    private String page;

    private String size;

    private String status;

    public Integer getPage() {
        try {
            int page = Integer.parseInt(this.page);
            return page <= 1 ? 0 : page - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getSize() {
        try {
            int size = Integer.parseInt(this.size);
            return size > 0 && size <= 50 ? size : 50;
        } catch (Exception e) {
            return 50;
        }
    }
}
