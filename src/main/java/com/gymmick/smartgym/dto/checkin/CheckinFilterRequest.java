package com.gymmick.smartgym.dto.checkin;

import com.gymmick.smartgym.entity.enums.CheckinStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckinFilterRequest {

    private String page;

    private String size;

    private CheckinStatus status;

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
