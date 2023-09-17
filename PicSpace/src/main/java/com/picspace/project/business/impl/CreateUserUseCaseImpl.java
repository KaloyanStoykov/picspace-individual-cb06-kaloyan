package com.picspace.project.business.impl;

import com.picspace.project.business.CreateUserUseCase;
import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.domain.CreateUserRequest;
import com.picspace.project.domain.CreateUserResponse;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.UserEntity;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private final UserRepository userRepository;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request){
        if(request.getAge() < 18){
            throw new ValidationException("User is underage (under 18 years of age");
        }

        UserEntity savedUser = saveNewUser(request);

        return CreateUserResponse.builder().userId(savedUser.getId()).message("User Created successfully!").build();

    }


    private UserEntity saveNewUser(CreateUserRequest request){
        UserEntity newUser = UserEntity.builder().name(request.getName()).lastName(request.getLastName()).password(request.getPassword()).age(request.getAge()).build();

        return userRepository.saveUser(newUser);
    }
}
