package io.eikon.authservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO {

    private String firstName;

    private String lastName;

    private String password;

    private String bio;
}
