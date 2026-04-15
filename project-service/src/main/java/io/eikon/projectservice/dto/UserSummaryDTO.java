package io.eikon.projectservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryDTO {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;
}
