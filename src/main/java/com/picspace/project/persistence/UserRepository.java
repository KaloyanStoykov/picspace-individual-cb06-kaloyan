package com.picspace.project.persistence;

import com.picspace.project.domain.User;


import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    void deleteById(Integer userId);

    List<User> getAllUsers();




}
