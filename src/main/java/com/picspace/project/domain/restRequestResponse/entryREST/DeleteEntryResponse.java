package com.picspace.project.domain.restRequestResponse.entryREST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class DeleteEntryResponse {
    private String message;
}
