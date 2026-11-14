package com.gymmick.smartgym.dto.dashboard;

import lombok.*;

@Getter
@Setter
public class UserFilterRequest {

    private String page;

    private String size;

    private String name;

    public Integer getPage() {
        try {
            Integer page = Integer.parseInt(this.page);

            if (page <= 1) {
                return 0;
            }

            return page - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getSize() {
        Integer limit = 50;

        try {
            Integer size = Integer.parseInt(this.size);

            if (size > 0 && size <= limit) {
                return size;
            }

            return limit;
        } catch (Exception e) {
            return limit;
        }
    }
}
