package com.picspace.project.controller;

import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.FilterDTO;
import com.picspace.project.domain.restRequestResponse.userREST.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;


    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetAllUsersResponse> getAllUsers(@RequestParam(defaultValue =  "0") int page, @RequestParam(defaultValue = "10") int size){
        GetAllUsersResponse getAllUsersResponse = userService.getAllUsers(page, size);
        return ResponseEntity.ok().body(getAllUsersResponse);
    }

    @PostMapping("/filter-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetFilteredUsersResponse> getFilteredUsers(@RequestBody List<FilterDTO> filterDTOList, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getFilteredUsers(filterDTOList, page, size));
    }

    @GetMapping("/count")
    public ResponseEntity<GetCountOfUsers> getCountofUsers(){
        return ResponseEntity.ok(userService.getCountofUsersRegistered());
    }

    @GetMapping("{id}")
    public ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getByUserId(userId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable("id") Long userId){
        DeleteUserResponse deleteUserResponse = userService.deleteUserById(userId);
        return ResponseEntity.ok().body(deleteUserResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable("id") Long userId, @RequestBody UpdateUserRequest request){
        UpdateUserResponse response = userService.updateUser(userId, request.getName(), request.getLastName(), request.getUsername(), request.getAge());
        return ResponseEntity.ok(response);
    }


}
