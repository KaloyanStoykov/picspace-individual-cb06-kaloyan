package com.picspace.project.controller;


import com.picspace.project.business.services.AuthenticationService;
import com.picspace.project.domain.restRequestResponse.authREST.JwtAuthenticationResponse;
import com.picspace.project.domain.restRequestResponse.authREST.SignInRequest;
import com.picspace.project.domain.restRequestResponse.authREST.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request){
        return authenticationService.signup(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

}
