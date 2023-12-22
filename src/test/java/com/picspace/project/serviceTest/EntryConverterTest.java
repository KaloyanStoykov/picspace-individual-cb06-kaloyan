package com.picspace.project.serviceTest;

import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.domain.Entry;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EntryConverterTest {
    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private EntryConverter entryConverter;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and inject them
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToEntity() {
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        // Arrange
        LocalDateTime registrationTime = LocalDateTime.now();
        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "pass1", 30, registrationTime, roles, entries);
        User user1 = new User(1L, "John", "Doe", "johndoe", "pass1", 30, registrationTime);

        Date date = new Date();
        Entry entry = new Entry(1L, user1, "content", date);

        when(userConverter.toEntity(user1)).thenReturn(userEntity1);

        // Act
        EntryEntity result = entryConverter.toEntity(entry);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity1, result.getEntryUser());
        assertEquals(entry.getContent(), result.getContent());
        assertEquals(date, result.getDateCreated());

        // Verify interactions
        verify(userConverter, times(1)).toEntity(user1);
    }

    @Test
    public void testToPojo() {
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleEntity);

        List<EntryEntity> entries = Collections.emptyList();

        // Arrange
        LocalDateTime registrationTime = LocalDateTime.now();
        UserEntity userEntity1 = new UserEntity(1L, "John", "Doe", "johndoe", "pass1", 30, registrationTime, roles, entries);
        User user1 = new User(2L, "John", "Doe", "johndoe", "pass1", 30, registrationTime);

        Date date = new Date();
        EntryEntity entryEntity = new EntryEntity(1L, userEntity1, "content", date);

        when(userConverter.toPojo(userEntity1)).thenReturn(user1);

        // Act
        Entry result = entryConverter.toPojo(entryEntity);

        // Assert
        assertNotNull(result);
        assertEquals(entryEntity.getId(), result.getPostId());
        assertEquals(user1, result.getUser());
        assertEquals(entryEntity.getContent(), result.getContent());
        assertEquals(date, result.getDateCreated());

        // Verify interactions
        verify(userConverter, times(1)).toPojo(userEntity1);
    }


}
