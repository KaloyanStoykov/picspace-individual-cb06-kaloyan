package com.picspace.project.domain.restRequestResponse.entryREST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCountOfEntriesResponse {
    private Long countOfEntries;
}
