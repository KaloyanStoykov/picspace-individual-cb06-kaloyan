package com.picspace.project.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User {


    @ToString.Exclude
    private Long id;

    private String name;
    private String lastName;

    private String username;
    @ToString.Exclude
    private String password;

    private Integer age;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registeredAt;
}
