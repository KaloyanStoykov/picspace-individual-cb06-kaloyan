package com.picspace.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DeleteUserResponse {
    private boolean success;
    private String message;
}
