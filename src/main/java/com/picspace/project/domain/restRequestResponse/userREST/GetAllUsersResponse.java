package com.picspace.project.domain.restRequestResponse.userREST;

import com.picspace.project.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.List;


@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class GetAllUsersResponse {
    private List<User> allUsers;
}
