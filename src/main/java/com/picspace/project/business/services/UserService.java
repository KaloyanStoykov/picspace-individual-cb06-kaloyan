package com.picspace.project.business.services;

import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo){
        this.userRepo = userRepo;
    }

    public User saveUser(User u){
        if(u.getAge() < 18){
            throw new UnderageUserException();
        }
        this.userRepo.saveUser(u);
        return u;
    }

    public void deleteUser(Integer userId){
        this.userRepo.deleteById(userId);
    }



    public List<User> getAllUsers(){
        return this.userRepo.getAllUsers();
    }
}
