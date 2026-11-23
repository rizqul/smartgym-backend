
package com.gymmick.smartgym.dto.transaction;

import org.springframework.transaction.TransactionStatus;

import com.gymmick.smartgym.entity.enums.TransactionType;

import lombok.*;

@Getter
@Setter
public class TransactionFilterRequest {

  private String page;

  private String size;

  private TransactionType type;

  private TransactionStatus status;

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
