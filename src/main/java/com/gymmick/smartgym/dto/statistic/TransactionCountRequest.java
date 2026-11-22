
package com.gymmick.smartgym.dto.statistic;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class TransactionCountRequest {

  @NotBlank(message = "The year field is required.")
  private int year;

}
