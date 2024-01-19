package com.picspace.project.serviceTest;

import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.business.exception.NoFilteredUsersFoundException;
import com.picspace.project.business.exception.PermissionDeniedException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.FilterDTO;
import com.picspace.project.domain.User;
import com.picspace.project.domain.restRequestResponse.userREST.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Mock
    private Authentication authentication;

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


        List<FilterDTO> filterDTOList = Arrays.asList(new FilterDTO("name", filterName));

        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "password123", 30, LocalDateTime.now(), null, null);
        UserEntity userEntity2 = new UserEntity(2L, "Jane", "Doe", "janedoe", "password456", 25, LocalDateTime.now(), null, null);

        List<UserEntity> filteredUserEntities = Arrays.asList(userEntity1);

        Page<UserEntity> userPage = new PageImpl<>(filteredUserEntities, PageRequest.of(page - 1, size), filteredUserEntities.size());

        when(userRepo.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(userPage);

        User user1 = User.builder()
                .name("John")
                .lastName("Doe")
                .username("johndoe")
                .password("password123")
                .age(30)
                .registeredAt(LocalDateTime.now())
                .build();

        when(userConverter.toPojo(userEntity1)).thenReturn(user1);

        GetFilteredUsersResponse response = userService.getFilteredUsers(filterDTOList, page, size);


        assertNotNull(response);
        assertFalse(response.getAllUsers().isEmpty());
        assertEquals(1, response.getAllUsers().size()); // Only 1 user should be returned as per the filter
        assertEquals(filteredUserEntities.size(), response.getTotalItems());
        assertEquals(1, response.getTotalPages());

        verify(userRepo, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(userConverter, times(1)).toPojo(userEntity1);
        verify(userConverter, never()).toPojo(userEntity2);
    }


    @Test
    public void testGetFilteredUsers_NoMatchingUsers() {
        int page = 1;
        int size = 10;
        String filterName = "John";

        List<FilterDTO> filterDTOList = Arrays.asList(new FilterDTO("name", filterName));

        List<UserEntity> noMatchingUserEntities = new ArrayList<>();

        Page<UserEntity> emptyUserPage = new PageImpl<>(noMatchingUserEntities, PageRequest.of(page - 1, size), noMatchingUserEntities.size());

        when(userRepo.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(emptyUserPage);

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

        LocalDateTime initialTime = LocalDateTime.now().minusDays(1);
        UserEntity existingUser = new UserEntity(1L, "Jane", "Doe", "janedoe", "pass2", 25, initialTime, roles, entries);

        when(userRepo.save(existingUser)).thenReturn(existingUser);

        UserEntity result = userService.save(existingUser);


        assertNotNull(result);
        assertEquals(initialTime, result.getRegisteredAt()); // Ensure registeredAt is unchanged
        assertEquals(existingUser, result);

        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        String username = "johndoe";
        UserEntity userEntity = new UserEntity(1L, "John", "Doe", username, "pass1", 30, LocalDateTime.now(), roles, entries);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());

        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String username = "unknownuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

    }


    @Test
    public void testUpdateUser_Success() {

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


        UpdateUserResponse updatedUserResponse = userService.updateUser(userId, newName, newLastName, newUsername, newAge);


        assertNotNull(updatedUserResponse);
        assertEquals("User updated successfully!", updatedUserResponse.getMessage());
        assertEquals(userId, updatedUserResponse.getUserId());
        verify(userRepo).findById(userId);
        verify(userRepo).save(any(UserEntity.class));
    }


    @Test
    public void testUpdateUser_UserNotFound() {

        Long userId = 2L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userId, "Name", "LastName", "Username", 25));
    }


    private void mockAuthentication(UserEntity user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testDeleteUserById_Admin_ShouldDeleteUser() {

        UserEntity adminUser = new UserEntity();
        adminUser.setId(1L);
        adminUser.setRoles(Arrays.asList(new RoleEntity(2L, "ROLE_ADMIN")));
        mockAuthentication(adminUser);

        Long userIdToDelete = 2L;
        when(userRepo.findById(userIdToDelete)).thenReturn(Optional.of(new UserEntity()));


        DeleteUserResponse response = userService.deleteUserById(userIdToDelete);


        assertEquals("User Deleted Successfully", response.getMessage());
        verify(userRepo).deleteById(userIdToDelete);
    }

    @Test
    public void testDeleteUserById_NonAdminUser_Failure() {

        UserEntity nonAdminUser = new UserEntity();
        nonAdminUser.setId(1L);
        nonAdminUser.setRoles(Arrays.asList(new RoleEntity(1L, "ROLE_USER")));
        mockAuthentication(nonAdminUser);

        Long userIdToDelete = 2L;


        assertThrows(PermissionDeniedException.class, () -> userService.deleteUserById(userIdToDelete));
    }

    @Test
    public void testDeleteUserById_UserNotFound() {

        UserEntity adminUser = new UserEntity();
        adminUser.setId(1L);
        adminUser.setRoles(Arrays.asList(new RoleEntity(2L, "ROLE_ADMIN")));
        mockAuthentication(adminUser);

        Long userIdToDelete = 2L;
        when(userRepo.findById(userIdToDelete)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(userIdToDelete));
    }

    @Test
    void testGetCountOfUsersRegisteredBetween() {

        Long expectedCount = 10L;
        when(userRepo.countUsersRegistered()).thenReturn(expectedCount);


        GetCountOfUsers actualCount = userService.getCountofUsersRegistered();


        assertEquals(expectedCount, actualCount.getCountOfUsers());
    }



}
