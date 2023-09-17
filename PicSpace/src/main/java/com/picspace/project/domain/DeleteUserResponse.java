package com.picspace.project.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteUserResponse {
    private boolean success;
    private String message;
}
