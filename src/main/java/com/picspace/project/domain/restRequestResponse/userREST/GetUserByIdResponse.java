package com.picspace.project.domain.restRequestResponse.userREST;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class GetUserByIdResponse {
    private Long id;
    private String name;
    private String lastName;

    private String username;
    @ToString.Exclude
    private String password;

    private Integer age;

    private LocalDateTime registeredAt;
}
