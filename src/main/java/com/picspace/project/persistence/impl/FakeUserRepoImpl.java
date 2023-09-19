package com.picspace.project.persistence.impl;

import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository

public class FakeUserRepoImpl implements UserRepository  {

    private static int NEXT_ID = 4;


    private final List<User> savedUsers;

    public FakeUserRepoImpl(){
        this.savedUsers = new ArrayList<>();
        this.savedUsers.add(new User(1,"Kaloyan", "Stoykov", "123", 23));
        this.savedUsers.add(new User(2,"John", "Doe", "123", 22));
        this.savedUsers.add(new User(3,"Nick", "Doe", "password", 20));

    }

    @Override
    public User saveUser(User user){

        user.setId(NEXT_ID);
        NEXT_ID++;
        this.savedUsers.add(user);


        return user;
    }

    @Override
    public void deleteById(Integer userId){
        this.savedUsers.removeIf(userEntity -> userEntity.getId().equals(userId));
    }

    @Override
    public List<User> getAllUsers(){
        return Collections.unmodifiableList(this.savedUsers);
    }


}
