package com.picspace.project.domain.restRequestResponse.userREST;

import com.picspace.project.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class GetFilteredUsersResponse {
    private List<User> allUsers;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}
