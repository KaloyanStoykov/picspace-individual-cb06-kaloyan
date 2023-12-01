package com.picspace.project.domain.restRequestResponse.authREST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    String name;
    String lastName;
    String username;
    String password;
    Integer age;
}
