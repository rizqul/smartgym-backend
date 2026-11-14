package com.gymmick.smartgym.dto.pagination;

import java.util.List;

import lombok.*;

@Data
@Builder
public class PaginationResponse<T> {

    private List<T> data;

    private PaginationMeta pagination;
}
