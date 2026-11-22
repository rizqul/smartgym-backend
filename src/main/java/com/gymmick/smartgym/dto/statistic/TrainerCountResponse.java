
package com.gymmick.smartgym.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerCountResponse {

  Long countTrainer;
  Long countTrainerWithSlot;
  Long countSlot;
  Long sumTransactionBookedTrainer;

}
