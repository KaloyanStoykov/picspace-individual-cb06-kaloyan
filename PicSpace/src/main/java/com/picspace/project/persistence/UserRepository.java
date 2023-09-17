package com.picspace.project.persistence;

import com.picspace.project.persistence.entity.UserEntity;

import java.util.List;

public interface UserRepository {
    UserEntity saveUser(UserEntity student);

    void deleteById(long studentId);

    List<UserEntity> getAllUsers();




}
