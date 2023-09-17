package com.picspace.project.business;

import com.picspace.project.domain.CreateUserRequest;
import com.picspace.project.domain.CreateUserResponse;

public interface CreateUserUseCase {
    CreateUserResponse createUser(CreateUserRequest request);
}
