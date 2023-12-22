package com.picspace.project.serviceTest;

import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.business.exception.NoFilteredUsersFoundException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.FilterDTO;
import com.picspace.project.domain.User;
import com.picspace.project.domain.restRequestResponse.userREST.GetAllUsersResponse;
import com.picspace.project.domain.restRequestResponse.userREST.GetFilteredUsersResponse;
import com.picspace.project.domain.restRequestResponse.userREST.GetUserByIdResponse;
import com.picspace.project.domain.restRequestResponse.userREST.UpdateUserResponse;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
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

        int page = 0;
        int size = 10;

        // Non-admin role
        RoleEntity userRoleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> userRoles = new ArrayList<>();
        userRoles.add(userRoleEntity);

        // Admin role
        RoleEntity adminRoleEntity = new RoleEntity(2L, "ROLE_ADMIN");
        List<RoleEntity> adminRoles = new ArrayList<>();
        adminRoles.add(adminRoleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        // Two non-admin users
        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "pass1", 30, LocalDateTime.now(), userRoles, entries);
        UserEntity userEntity2 = new UserEntity(2L, "Jane", "Doe", "janedoe", "pass2", 25, LocalDateTime.now(), userRoles, entries);

        // One admin user (should be filtered out)
        UserEntity adminEntity = new UserEntity(3L, "Admin", "User", "adminuser", "adminpass", 35, LocalDateTime.now(), adminRoles, entries);

        List<UserEntity> allUserEntities = Arrays.asList(userEntity1, userEntity2, adminEntity);
        Page<UserEntity> userEntityPage = new PageImpl<>(Arrays.asList(userEntity1, userEntity2), PageRequest.of(page, size), 2);

        User user1 = new User(1L, "John", "Doe", "johndoe", "pass1", 30, LocalDateTime.now());
        User user2 = new User(2L, "Jane", "Doe", "janedoe", "pass2", 25, LocalDateTime.now());
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepo.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(userEntityPage);
        when(userConverter.toPojo(userEntity1)).thenReturn(user1);
        when(userConverter.toPojo(userEntity2)).thenReturn(user2);

        // Act
        GetAllUsersResponse response = userService.getAllUsers(page, size);

        // Assert
        assertNotNull(response);
        assertEquals(expectedUsers.size(), response.getAllUsers().size());
        assertEquals(expectedUsers, response.getAllUsers());

        // Verify interactions
        verify(userRepo, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(userConverter, times(1)).toPojo(userEntity1);
        verify(userConverter, times(1)).toPojo(userEntity2);
    }


    @Test
    public void testGetFilteredUsers_Successful() {
        // Arrange
        int page = 1;
        int size = 10;
        String filterName = "John";

        // Set up filter to search for users with the name "John"
        List<FilterDTO> filterDTOList = Arrays.asList(new FilterDTO("name", filterName));

        // Create mock UserEntities, only one matching the filter
        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "password123", 30, LocalDateTime.now(), null, null);
        UserEntity userEntity2 = new UserEntity(2L, "Jane", "Doe", "janedoe", "password456", 25, LocalDateTime.now(), null, null);

        // Only include the user that matches the filter
        List<UserEntity> filteredUserEntities = Arrays.asList(userEntity1);

        // Mock the Page object to return only the filtered user
        Page<UserEntity> userPage = new PageImpl<>(filteredUserEntities, PageRequest.of(page - 1, size), filteredUserEntities.size());

        // Mocking findAll with a Specification<UserEntity> and a Pageable
        when(userRepo.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(userPage);

        // Create User DTO for the filtered user
        User user1 = User.builder()
                .name("John")
                .lastName("Doe")
                .username("johndoe")
                .password("password123")
                .age(30)
                .registeredAt(LocalDateTime.now())
                .build();

        // Mock the conversion of the UserEntity to User DTO
        when(userConverter.toPojo(userEntity1)).thenReturn(user1);

        // Act
        GetFilteredUsersResponse response = userService.getFilteredUsers(filterDTOList, page, size);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAllUsers().isEmpty());
        assertEquals(1, response.getAllUsers().size()); // Only 1 user should be returned as per the filter
        assertEquals(filteredUserEntities.size(), response.getTotalItems());
        assertEquals(1, response.getTotalPages());

        // Verify the interactions
        verify(userRepo, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(userConverter, times(1)).toPojo(userEntity1);
        // Ensure no interaction with userEntity2 as it does not match the filter
        verify(userConverter, never()).toPojo(userEntity2);
    }


    @Test
    public void testGetFilteredUsers_NoMatchingUsers() {
        // Arrange
        int page = 1;
        int size = 10;
        String filterName = "John";

        // Set up filter to search for users with the name "John"
        List<FilterDTO> filterDTOList = Arrays.asList(new FilterDTO("name", filterName));

        // Create an empty list to simulate no matching users
        List<UserEntity> noMatchingUserEntities = new ArrayList<>();

        // Mock the Page object to return an empty list
        Page<UserEntity> emptyUserPage = new PageImpl<>(noMatchingUserEntities, PageRequest.of(page - 1, size), noMatchingUserEntities.size());

        // Mocking findAll with a Specification<UserEntity> and a Pageable to return an empty page
        when(userRepo.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(emptyUserPage);

        // Act



        assertThrows(NoFilteredUsersFoundException.class, () -> {
            userService.getFilteredUsers(filterDTOList, page, size);
        });
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


    @Test
    public void testUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        String newName = "NewName";
        String newLastName = "NewLastName";
        String newUsername = "NewUsername";
        int newAge = 35;

        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setName("OldName");
        existingUser.setLastName("OldLastName");
        existingUser.setUsername("OldUsername");
        existingUser.setAge(30);

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UpdateUserResponse updatedUserResponse = userService.updateUser(userId, newName, newLastName, newUsername, newAge);

        // Assert
        assertNotNull(updatedUserResponse);
        assertEquals("User updated successfully!", updatedUserResponse.getMessage());
        assertEquals(userId, updatedUserResponse.getUserId());
        verify(userRepo).findById(userId);
        verify(userRepo).save(any(UserEntity.class));
    }


    @Test
    public void testUpdateUser_UserNotFound() {
        // Arrange
        Long userId = 2L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userId, "Name", "LastName", "Username", 25));
    }



}
