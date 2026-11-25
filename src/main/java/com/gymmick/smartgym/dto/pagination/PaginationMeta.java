package com.gymmick.smartgym.dto.pagination;

import java.util.List;

import lombok.*;

@Data
@Builder
public class PaginationMeta {

    private Integer size;

    private Integer currentPage;

    private Integer totalPages;

    private Long totalData;

    private String prevUrl;

    private String nextUrl;

    private List<PaginationLink> links;
}
