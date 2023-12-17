package com.picspace.project.domain.restRequestResponse.userREST;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    Long userId;
    String name;
    String lastName;
    String username;
    int age;
}
