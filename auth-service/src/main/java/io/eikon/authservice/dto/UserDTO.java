package io.eikon.authservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private String role;

    private String bio;
}
