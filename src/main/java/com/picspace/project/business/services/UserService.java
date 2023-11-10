package com.picspace.project.business.services;


import com.picspace.project.business.exception.UserNotFoundException;

import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo){
        this.userRepo = userRepo;
    }

    public GetUserByIdResponse getByUserId(Long id) {
        Optional<UserEntity> foundUser = userRepo.findById(id);



        if(foundUser.isPresent()){
            UserEntity userEntity = foundUser.get();
            return GetUserByIdResponse.builder().id(userEntity.getId()).name(userEntity.getName()).lastName(userEntity.getLastName()).age(userEntity.getAge()).username(userEntity.getUsername()).password(userEntity.getPassword()).registeredAt(userEntity.getRegisteredAt()).build();
        }

        throw new UserNotFoundException();
    }


}
