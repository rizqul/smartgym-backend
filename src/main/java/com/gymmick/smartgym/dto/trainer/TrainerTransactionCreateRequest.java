
package com.gymmick.smartgym.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class TrainerTransactionCreateRequest {

  @NotBlank(message = "The slot id field is required.")
  private String slotId;
}
