package com.picspace.project.controller;

import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;


    @GetMapping("{id}")
    public ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getByUserId(userId));
    }

}
