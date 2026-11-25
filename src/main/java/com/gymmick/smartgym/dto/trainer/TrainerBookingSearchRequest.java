package com.gymmick.smartgym.dto.trainer;

import org.apache.commons.lang3.math.NumberUtils;

import lombok.*;

@Getter
@Setter
public class TrainerBookingSearchRequest {

    private String search;

    private String page;

    private String size;

    public Integer getPage() {

        Integer page = NumberUtils.toInt(this.page, 0);

        return page > 1 ? page - 1 : 0;
    }

    public Integer getSize() {

        Integer size = NumberUtils.toInt(this.size, 10);

        return size <= 50 ? size : 50;
    }
}
