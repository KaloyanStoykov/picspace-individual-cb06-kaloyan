package com.picspace.project.serviceTest;

import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class UserConverterTest {
    @Mock
    private UserConverter userConverter;

    private RoleEntity roleEntity;

    private List<RoleEntity> roles;

    private List<EntryEntity> entryEntities;

    @BeforeEach
    private void setUp(){
        MockitoAnnotations.openMocks(this);
        roleEntity = new RoleEntity(1L, "ROLE_USER");
        roles = new ArrayList<>();
        roles.add(roleEntity);
        entryEntities = Collections.emptyList();
    }

    @Test
    public void testToEntity() {
        List<EntryEntity> entries = Collections.emptyList();
        // Arrange
        LocalDateTime registrationTime = LocalDateTime.now();
        User user = new User("John", "Doe", "johndoe", "pass123", 30, registrationTime);
        UserEntity userEntity1 = UserEntity.builder().name("John").lastName("Doe").username("johndoe").password("pass123").age(30).registeredAt(registrationTime).build();


        when(userConverter.toEntity(user)).thenReturn(userEntity1);

        // Act
        UserEntity result = userConverter.toEntity(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getAge(), result.getAge());
        assertEquals(user.getRegisteredAt(), result.getRegisteredAt());
    }

    @Test
    public void testToPojo() {
        // Arrange


        LocalDateTime registrationTime = LocalDateTime.now();

        UserEntity userEntity = UserEntity.builder().name("Kal").lastName("Stoykov").username("kiko").age(19).registeredAt(registrationTime).build();
        User user = new User("Kal", "Stoykov", "kiko", "pass123", 19, registrationTime);


        when(userConverter.toPojo(userEntity)).thenReturn(user);

        // Act
        User result = userConverter.toPojo(userEntity);
        // Assert
        assertNotNull(result);
        assertEquals(userEntity.getName(), result.getName());
        assertEquals(userEntity.getLastName(), result.getLastName());
        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getAge(), result.getAge());
        assertEquals(userEntity.getRegisteredAt(), result.getRegisteredAt());
    }


}
