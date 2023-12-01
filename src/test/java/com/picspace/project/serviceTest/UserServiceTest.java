package com.picspace.project.serviceTest;

import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.exception.UsernameAlreadyExistsException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.User;
import com.picspace.project.domain.restRequestResponse.userREST.GetAllUsersResponse;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    private UserDetailsService userDetailsService;

    @Mock
    private UserConverter userConverter;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userDetailsService = new UserService(userRepo, userConverter).userDetailsService();

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

    @Test
    public void testGetAllUsers() {

        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        // Arrange
        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "pass1", 30, LocalDateTime.now(), roles, entries);
        UserEntity userEntity2 = new UserEntity(2L, "Jane", "Doe", "janedoe", "pass2", 25, LocalDateTime.now(), roles, entries);
        List<UserEntity> mockUserEntities = Arrays.asList(userEntity1, userEntity2);

        User user1 = new User("John", "Doe", "johndoe", "pass1", 30, LocalDateTime.now());
        User user2 = new User("Jane", "Doe", "janedoe", "pass2", 25, LocalDateTime.now());
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepo.findAll()).thenReturn(mockUserEntities);
        when(userConverter.toPojo(userEntity1)).thenReturn(user1);
        when(userConverter.toPojo(userEntity2)).thenReturn(user2);

        // Act
        GetAllUsersResponse response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(expectedUsers.size(), response.getAllUsers().size());
        assertEquals(expectedUsers, response.getAllUsers());

        // Verify interactions
        verify(userRepo, times(1)).findAll();
        verify(userConverter, times(1)).toPojo(userEntity1);
        verify(userConverter, times(1)).toPojo(userEntity2);
    }

    @Test
    public void testSaveExistingUser() {
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        // Arrange
        LocalDateTime initialTime = LocalDateTime.now().minusDays(1);
        UserEntity existingUser = new UserEntity(1L, "Jane", "Doe", "janedoe", "pass2", 25, initialTime, roles, entries);

        when(userRepo.save(existingUser)).thenReturn(existingUser);

        // Act
        UserEntity result = userService.save(existingUser);

        // Assert
        assertNotNull(result);
        assertEquals(initialTime, result.getRegisteredAt()); // Ensure registeredAt is unchanged
        assertEquals(existingUser, result);

        // Verify interactions
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();
        // Arrange
        String username = "johndoe";
        UserEntity userEntity = new UserEntity(1L, "John", "Doe", username, "pass1", 30, LocalDateTime.now(), roles, entries);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());

        // Verify interactions
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "unknownuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // Act

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

        // Assert is handled by the expected exception
    }


}
