
package com.gymmick.smartgym.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberCountResponse {

  Long memberCount;

  Long memberActiveCount;

  Long memberInactiveCount;

  Long newMemberCount;

}
