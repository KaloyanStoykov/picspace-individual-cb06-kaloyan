package com.picspace.project.domain.restRequestResponse.userREST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateUserResponse {
    String message;
    Long userId;
}
