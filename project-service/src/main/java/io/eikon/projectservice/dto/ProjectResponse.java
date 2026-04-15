package io.eikon.projectservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {

    private String id;

    private String clientId;

    private String title;

    private String description;

    private String category;

    private Double budget;

    private String status;

    private UserSummaryDTO clientDetails;
}
