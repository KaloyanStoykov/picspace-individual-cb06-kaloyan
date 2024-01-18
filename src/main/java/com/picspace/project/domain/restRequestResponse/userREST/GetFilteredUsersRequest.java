package com.picspace.project.domain.restRequestResponse.userREST;

import com.picspace.project.domain.FilterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFilteredUsersRequest {
    private List<FilterDTO> filterDTOList;
}
