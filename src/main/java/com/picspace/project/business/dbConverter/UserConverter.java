package com.picspace.project.business.dbConverter;

import com.picspace.project.domain.User;
import com.picspace.project.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements EntityConverter<UserEntity, User>{


    public UserConverter(){

    }
    @Override
    public UserEntity toEntity(User user){
        return UserEntity.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .age(user.getAge())
                .registeredAt(user.getRegisteredAt())
                .build();
    }

    @Override
    public User toPojo(UserEntity userEntity){
        return User.builder()
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .age(userEntity.getAge())
                .registeredAt(userEntity.getRegisteredAt())
                .build();
    }
}
