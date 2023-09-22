package com.picspace.project.domain.restClasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateUserResponse {
    private Integer userId;
    private String message;
}
