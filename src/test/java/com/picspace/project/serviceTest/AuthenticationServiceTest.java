package com.picspace.project.serviceTest;

import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.exception.UsernameAlreadyExistsException;
import com.picspace.project.business.services.AuthenticationService;
import com.picspace.project.business.services.JwtService;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.restRequestResponse.authREST.JwtAuthenticationResponse;
import com.picspace.project.domain.restRequestResponse.authREST.SignInRequest;
import com.picspace.project.domain.restRequestResponse.authREST.SignUpRequest;
import com.picspace.project.persistence.RoleRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private RoleEntity defaultRole;
    @Mock
    private UserEntity mockUserEntity;

    private AutoCloseable closeable;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockUserEntity = UserEntity.builder()
                .id(1L)
                .username("existingUser")
                .password("encryptedPassword")
                .build();
    }

    @AfterEach
    void closeService() throws Exception{
        closeable.close();
    }

    @Test
    void testSuccessfulSignup() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userService.save(any(UserEntity.class))).thenReturn(mockUserEntity);
        when(jwtService.generateToken(any(UserEntity.class), anyLong())).thenReturn("mockJwtToken");
        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");

        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        request.setName("Kal");
        request.setLastName("Stoykov");
        request.setAge(18);

        // Act
        JwtAuthenticationResponse response = authService.signup(request);

        // Assert
        assertNotNull(response.getToken());
        assertEquals("mockJwtToken", response.getToken());
        // You can also add assertions to check the behavior of mocks, if necessary
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userService, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testUsernameAlreadyExistsException() {
        // Arrange
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(mockUserEntity));

        SignUpRequest request = new SignUpRequest();
        request.setUsername("existingUser");
        request.setPassword("password");
        request.setName("Kal");
        request.setLastName("Stoykov");
        request.setAge(18);



        // Act and Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            authService.signup(request);
        });

        // Verify that no other methods are called after finding the username
        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(roleRepository, never()).findByName(anyString());
        verify(userService, never()).save(any(UserEntity.class));
    }

    @Test
    void testDefaultRoleNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_MANAGER")).thenReturn(Optional.empty());

        SignUpRequest request = new SignUpRequest();
        request.setUsername("newUser");
        request.setPassword("password");


        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(request);
        });

        // Verify that the userRepository is called, but userService.save is never reached
        verify(userRepository, times(1)).findByUsername("newUser");
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userService, never()).save(any(UserEntity.class));
    }



    @Test
    void testSignIn_ShouldThrowException(){
        SignInRequest requestLogin = new SignInRequest();
        requestLogin.setUsername("randomUsername");
        requestLogin.setPassword("randomPassword");

        // Mocking userRepository to return an empty Optional
        when(userRepository.findByUsername("randomUsername")).thenReturn(Optional.empty());

        // Expecting the UsernameNotFoundException to be thrown
        assertThrows(UsernameNotFoundException.class, () -> authService.signin(requestLogin));

        // Verifying userRepository was called with the username
        verify(userRepository, times(1)).findByUsername("randomUsername");

        // Verifying jwtService.generateToken was never called
        verify(jwtService, never()).generateToken(any(), anyLong());
    }


}
