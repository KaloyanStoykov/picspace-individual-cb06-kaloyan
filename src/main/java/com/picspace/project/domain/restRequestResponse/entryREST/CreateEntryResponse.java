package com.picspace.project.domain.restRequestResponse.entryREST;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntryResponse {
    private Long postId;
    private String message;
}
