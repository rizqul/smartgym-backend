
package com.gymmick.smartgym.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCountResponse {

  Long sumTransactionYearly;
  Long transactionSuccess;
  Long transactionFailed;
  Long transactionProcess;

}
