package com.picspace.project.domain.restClasses;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CreateUserRequest {


    @NotBlank
    private String name;
    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @NotNull
    @Min(18)
    @Max(120)
    private Integer age;
}
