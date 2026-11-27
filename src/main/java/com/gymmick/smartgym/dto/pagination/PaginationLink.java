package com.gymmick.smartgym.dto.pagination;

public record PaginationLink(String label, boolean active, String url) {

    public static PaginationLink pageLink(int label, boolean active, String url) {
        return new PaginationLink(String.valueOf(label), active, url);
    }

    public static PaginationLink ellipsisLink() {
        return new PaginationLink("...", false, null);
    }
}
