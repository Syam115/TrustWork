package io.eikon.authservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String userId;

    private String email;

    private String role;

    private String accessToken;

    private String tokenType = "Bearer";
}
