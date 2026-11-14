package com.gymmick.smartgym.builder;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import com.gymmick.smartgym.dto.pagination.*;

public class PaginationLinkBuilder {

    private static final int ELLIPSIS = -1;

    public static PaginationMeta build(int currentPage, int totalPages, int size, long totalData,
            IntFunction<String> urlFactory) {

        if (totalPages <= 0) {
            PaginationMeta paginationMeta = PaginationMeta.builder()
                    .size(size)
                    .currentPage(1)
                    .totalPages(1)
                    .totalData(totalData)
                    .prevUrl(null)
                    .nextUrl(null)
                    .links(List.of())
                    .build();

            return paginationMeta;
        }

        int current = Math.max(1, Math.min(currentPage + 1, totalPages));

        List<PaginationLink> links = new ArrayList<>();
        for (int label : window(current, totalPages)) {
            if (label == ELLIPSIS) {
                links.add(PaginationLink.ellipsisLink());
            } else {
                links.add(PaginationLink.pageLink(label, label == current, urlFactory.apply(label)));
            }
        }

        String prevUrl = current > 1 ? urlFactory.apply(current - 1) : null;
        String nextUrl = current < totalPages ? urlFactory.apply(current + 1) : null;

        PaginationMeta paginationMeta = PaginationMeta.builder()
                .size(size)
                .currentPage(current)
                .totalPages(totalPages)
                .totalData(totalData)
                .prevUrl(prevUrl)
                .nextUrl(nextUrl)
                .links(links)
                .build();

        return paginationMeta;
    }

    private static List<Integer> window(int current, int total) {
        List<Integer> result = new ArrayList<>();
        System.out.println(current);
        System.out.println(total);

        if (total <= 5) {
            IntStream.rangeClosed(1, total).forEach(result::add);
            return result;
        }

        if (current <= 3) {
            IntStream.rangeClosed(1, 4).forEach(result::add);
            result.add(ELLIPSIS);
            result.add(total);
        } else if (current >= total - 3) {
            result.add(ELLIPSIS);
            IntStream.rangeClosed(total - 4, total).forEach(result::add);
        } else {
            result.add(ELLIPSIS);
            result.add(current - 1);
            result.add(current);
            result.add(current + 1);
            result.add(ELLIPSIS);
            result.add(total);
        }

        return result;
    }
}
