package com.picspace.project.controller;

import com.picspace.project.business.CreateUserUseCase;
import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.domain.CreateUserRequest;
import com.picspace.project.domain.CreateUserResponse;
import com.picspace.project.persistence.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        try {
            CreateUserResponse userResponse = createUserUseCase.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        } catch (UnderageUserException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(CreateUserResponse.builder().message(e.getMessage()).build());
        }
    }
}
