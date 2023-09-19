package com.picspace.project.domain;

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
