package com.picspace.project.business.services;

import com.picspace.project.business.exception.InvalidUserIdException;
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

        this.userRepo.saveUser(u);
        return u;
    }

    public boolean deleteUser(Integer userId){



        this.userRepo.deleteById(userId);
        return true;
    }

    public User getByUserId(Integer id) {
        return this.userRepo.getAllUsers()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers(){
        return this.userRepo.getAllUsers();
    }
}
