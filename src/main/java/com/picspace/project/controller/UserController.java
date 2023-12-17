package com.picspace.project.controller;

import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.restRequestResponse.userREST.GetAllUsersResponse;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.domain.restRequestResponse.userREST.UpdateUserRequest;
import com.picspace.project.domain.restRequestResponse.userREST.UpdateUserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;


    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetAllUsersResponse> getAllUsers(){
        GetAllUsersResponse getAllUsersResponse = userService.getAllUsers();
        return ResponseEntity.ok().body(getAllUsersResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getByUserId(userId));
    }

    @PutMapping("{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable("id") Long userId, @RequestBody UpdateUserRequest request){
        UpdateUserResponse response = userService.updateUser(userId, request.getName(), request.getLastName(), request.getUsername(), request.getAge());
        return ResponseEntity.ok(response);
    }


}
