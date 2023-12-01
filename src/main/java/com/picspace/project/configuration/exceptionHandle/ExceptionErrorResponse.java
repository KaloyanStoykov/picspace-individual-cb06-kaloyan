package com.picspace.project.configuration.exceptionHandle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionErrorResponse {
    private String status;
    private String message;
}
