package com.picspace.project.controller;

import com.picspace.project.business.exception.UnderageUserException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.DeleteUserResponse;
import com.picspace.project.domain.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.picspace.project.domain.CreateUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid User user){

        try{
            userService.saveUser(user);
            CreateUserResponse response = new CreateUserResponse(user.getId(), "User created successfully!");
            return ResponseEntity.ok(response);
        }
        catch (UnderageUserException underageUserException){
            CreateUserResponse response = new CreateUserResponse(user.getId(), String.format("%s",underageUserException.getMessage()));
            return ResponseEntity.badRequest().body(response);

        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<DeleteUserResponse> deleteUserById(@PathVariable(value = "id") final Integer id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok(new DeleteUserResponse(true, String.format("User with %d deleted successfully", id)));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new DeleteUserResponse(false, String.format("User with %d deleted successfully", id)));
        }



    }
}
