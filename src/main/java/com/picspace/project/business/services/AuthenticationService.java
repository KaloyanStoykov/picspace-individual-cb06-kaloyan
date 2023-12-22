package com.picspace.project.business.services;


import com.picspace.project.business.exception.UsernameAlreadyExistsException;
import com.picspace.project.configuration.JwtService;
import com.picspace.project.domain.restRequestResponse.authREST.JwtAuthenticationResponse;
import com.picspace.project.domain.restRequestResponse.authREST.SignUpRequest;
import com.picspace.project.domain.restRequestResponse.authREST.SignInRequest;
import com.picspace.project.persistence.RoleRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        RoleEntity defaultRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new IllegalArgumentException("Default role not found!"));

        var user = UserEntity
                .builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .roles(Collections.singletonList(defaultRole))
                .build();

        user = userService.save(user);
        Long userId = user.getId();

        var jwt = jwtService.generateToken(user, userId);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        var jwt = jwtService.generateToken(user, user.getId());
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
