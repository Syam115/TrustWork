package io.eikon.projectservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidResponse {

    private String id;

    private String freelancerId;

    private Double bidAmount;

    private String proposal;

    private String status;

    private UserSummaryDTO freelancerDetails;
}
