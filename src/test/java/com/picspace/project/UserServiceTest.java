package com.picspace.project;

import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.impl.FakeUserRepoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserServiceTest {
    private UserService userService;

    @Test
    void saveUser_shouldReturnUser_andCreateUserInRepo() {


        User newUser = new User();
        newUser.setName("John");
        newUser.setLastName("Winn");
        newUser.setPassword("123");
        newUser.setAge(22);

        userService.saveUser(newUser);


        // New User should be at index 3
        User actualUser = userService.getAllUsers().get(3);

        assertEquals(newUser, actualUser);



    }


    


    @Test
    void deleteUser_ShouldRemoveUserFromRepo(){
        int userId = 3;

        assertTrue(userService.deleteUser(userId));

    }

    @Test
    void getAllUsers_ShouldReturnAllUsers(){
        int expectedCount = 3;
        int actualCount = userService.getAllUsers().size();

        List<User> expectedUsers = new ArrayList<>(Arrays.asList(
                new User(1, "Kaloyan", "Stoykov", "123", 23),
                new User(2, "John", "Doe", "123", 22),
                new User(3, "Nick", "Doe", "password", 20)
        ));


        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedCount, actualCount);
        assertEquals(expectedUsers, actualUsers);

    }


    @BeforeEach
    public void setUp(){
        userService = new UserService(new FakeUserRepoImpl());
    }
}
