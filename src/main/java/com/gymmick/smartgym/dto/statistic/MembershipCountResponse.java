
package com.gymmick.smartgym.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembershipCountResponse {

  Long totalMembershipIncome;

  Long countMembershipPackage;

}
