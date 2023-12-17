package com.picspace.project.business.services;


import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.business.exception.UserNotFoundException;

import com.picspace.project.domain.User;
import com.picspace.project.domain.restRequestResponse.userREST.GetAllUsersResponse;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.domain.restRequestResponse.userREST.UpdateUserResponse;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserConverter userConverter;

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
            }
        };
    }

    public UserEntity save(UserEntity newUser){
        if(newUser.getId() == null){
            newUser.setRegisteredAt(LocalDateTime.now());
        }
        return userRepo.save(newUser);
    }

    public GetUserByIdResponse getByUserId(Long id) {
        Optional<UserEntity> foundUser = userRepo.findById(id);



        if(foundUser.isPresent()){
            UserEntity userEntity = foundUser.get();
            return GetUserByIdResponse.builder().id(userEntity.getId()).name(userEntity.getName()).lastName(userEntity.getLastName()).age(userEntity.getAge()).username(userEntity.getUsername()).password(userEntity.getPassword()).registeredAt(userEntity.getRegisteredAt()).build();
        }

        throw new UserNotFoundException();
    }


    public GetAllUsersResponse getAllUsers(){
        List<UserEntity> allUserEntities = userRepo.findAll();
        List<User> users = new ArrayList<>();

        for(UserEntity userEntity: allUserEntities){
            User user = userConverter.toPojo(userEntity);
            users.add(user);
        }

        return GetAllUsersResponse.builder().allUsers(users).build();

    }

    public UpdateUserResponse updateUser(Long userId, String name, String lastName, String username, int age) {
        return userRepo.findById(userId)
                .map(user -> {
                    user.setName(name);
                    user.setLastName(lastName);
                    user.setUsername(username);
                    user.setAge(age);
                    UserEntity userEntity = userRepo.save(user);

                    UpdateUserResponse response = new UpdateUserResponse("User updated successfully!", userEntity.getId());

                    return response;


                })
                .orElseThrow(UserNotFoundException::new);
    }


}
