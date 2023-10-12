package com.picspace.project.domain.restRequestResponse.entryREST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntryRequest {
    private Integer userId;
    private String content;

}
