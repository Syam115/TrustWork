package io.eikon.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidRequest {

    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be greater than 0")
    private Double bidAmount;

    @NotBlank(message = "Proposal is required")
    private String proposal;
}
