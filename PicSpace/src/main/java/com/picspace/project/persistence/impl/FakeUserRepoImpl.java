package com.picspace.project.persistence.impl;

import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository

public class FakeUserRepoImpl implements UserRepository  {

    private static long NEXT_ID = 1;


    private final List<UserEntity> savedUsers;

    public FakeUserRepoImpl(){
        this.savedUsers = new ArrayList<>();
    }

    @Override
    public UserEntity saveUser(UserEntity user){
        if(user.getId() == null){
            user.setId(NEXT_ID);
            NEXT_ID++;
            this.savedUsers.add(user);
        }

        return user;
    }

    @Override
    public void deleteById(long userId){
        this.savedUsers.removeIf(userEntity -> userEntity.getId().equals(userId));
    }

    @Override
    public List<UserEntity> getAllUsers(){
        return Collections.unmodifiableList(this.savedUsers);
    }


}
