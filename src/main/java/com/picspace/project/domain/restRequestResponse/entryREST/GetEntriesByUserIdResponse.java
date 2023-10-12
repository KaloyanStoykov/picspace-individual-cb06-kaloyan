package com.picspace.project.domain.restRequestResponse.entryREST;


import com.picspace.project.domain.Entry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetEntriesByUserIdResponse {
    private List<Entry> allUserEntries;
}
