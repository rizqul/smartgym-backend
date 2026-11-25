package com.gymmick.smartgym.dto.transaction;

import org.apache.commons.lang3.math.NumberUtils;

import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Getter
@Setter
public class TransactionSearchRequest {

    private String search;

    private String type;

    private String status;

    private String page;

    private String size;

    public TransactionType getType() {
        if (this.type == null) {
            return null;
        }

        String type = this.type.toUpperCase();

        if (TransactionType.isValid(type)) {
            return TransactionType.valueOf(type);
        }

        return null;
    }

    public TransactionStatus getStatus() {
        if (this.status == null) {
            return null;
        }

        String status = this.status.toUpperCase();

        if (TransactionStatus.isValid(status)) {
            return TransactionStatus.valueOf(status);
        }

        return null;
    }

    public Integer getPage() {

        Integer page = NumberUtils.toInt(this.page, 0);

        return page > 1 ? page - 1 : 0;
    }

    public Integer getSize() {

        Integer size = NumberUtils.toInt(this.size, 10);

        return size <= 50 ? size : 50;
    }
}
