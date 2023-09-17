package com.picspace.project.domain;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @ToString.Exclude
    private Long id;

    private String name;
    private String lastName;

    @ToString.Exclude
    private String password;

    private Integer age;

}
