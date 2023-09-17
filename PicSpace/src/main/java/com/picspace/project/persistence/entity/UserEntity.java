package com.picspace.project.persistence.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEntity {

    private Long id;

    private String name;
    private String lastName;


    private String password;

    private Integer age;
}
