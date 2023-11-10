package com.picspace.project.serviceTest;

import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void closeService() throws Exception{
        closeable.close();
    }

    @Test
    void getUserById_shouldReturnGetUserByIdResponse(){
        Long userId = 1L;
        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        mockUser.setName("John");
        mockUser.setLastName("Doe");
        mockUser.setAge(30);
        mockUser.setUsername("johndoe");
        mockUser.setPassword("password");
        mockUser.setRegisteredAt(LocalDateTime.now());
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));

        
        GetUserByIdResponse actualResponse = userService.getByUserId(userId);

        
        assertEquals(userId, actualResponse.getId());
        assertEquals("John", actualResponse.getName());
        assertEquals("Doe", actualResponse.getLastName());
        assertEquals(30, actualResponse.getAge());
        assertEquals("johndoe", actualResponse.getUsername());
        assertEquals("password", actualResponse.getPassword());
    }

    @Test
    public void testGetUserByIdNotFound() {
        
        Long userId = 2L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        
        assertThrows(UserNotFoundException.class, () -> userService.getByUserId(userId));
    }
}
