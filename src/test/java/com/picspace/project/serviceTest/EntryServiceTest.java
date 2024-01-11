package com.picspace.project.serviceTest;


import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.services.EntryService;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryRequest;
import com.picspace.project.domain.restRequestResponse.entryREST.GetEntriesByUserIdResponse;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EntryServiceTest {

    @Mock
    private EntryRepository mockEntryRepo;

    @Mock
    private UserRepository mockUserRepo;

    @Mock
    private EntryConverter mockEntryConverter;

    private EntryService entryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entryService = new EntryService(mockEntryRepo, mockEntryConverter, mockUserRepo);
    }


    @Test
    void getByUserId_UserFound_ShouldReturnEntries() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity(); // Assume this is correctly initialized
        EntryEntity entryEntity1 = new EntryEntity(); // Assume this is correctly initialized
        EntryEntity entryEntity2 = new EntryEntity(); // Assume this is correctly initialized
        Entry entry1 = new Entry(); // Assume this is correctly initialized
        Entry entry2 = new Entry(); // Assume this is correctly initialized

        when(mockUserRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockEntryRepo.findByUserId(userId)).thenReturn(Arrays.asList(entryEntity1, entryEntity2));
        when(mockEntryConverter.toPojo(entryEntity1)).thenReturn(entry1);
        when(mockEntryConverter.toPojo(entryEntity2)).thenReturn(entry2);

        GetEntriesByUserIdResponse response = entryService.getByUserId(userId);

        assertNotNull(response);
        assertEquals(2, response.getAllUserEntries().size());
        assertTrue(response.getAllUserEntries().containsAll(Arrays.asList(entry1, entry2)));
    }


    @Test
    void getEntriesByUserId_shouldThrowUserNotFoundException() {
        Long nonExistentUserId = 99L; // An ID that does not exist in the repository

        // Mock the userRepository to return empty for non-existing user
        UserRepository mockUserRepo = mock(UserRepository.class);
        when(mockUserRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Mock the EntryRepository and EntryConverter
        EntryRepository mockEntryRepo = mock(EntryRepository.class);
        EntryConverter entryConverter = mock(EntryConverter.class);

        // Create an instance of EntryService with mocked repositories
        EntryService entryService = new EntryService(mockEntryRepo, entryConverter, mockUserRepo);

        // Assert that UserNotFoundException is thrown
        assertThrows(UserNotFoundException.class, () -> {
            entryService.getByUserId(nonExistentUserId);
        });

        // Verify that entryRepo.findByUserId is not called
        verify(mockEntryRepo, never()).findByUserId(nonExistentUserId);
    }

    @Test
    void createEntry_shouldCreateEntry(){
        //Arrange
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(1L, "Test content" );
        EntryRepository mockEntryRepo = mock(EntryRepository.class);



        Long userId = createEntryRequest.getUserId();
        //Call mock user repo in order to get user
        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        UserRepository userMockRepository = mock(UserRepository.class);



        when(userMockRepository.findById(userId)).thenReturn(Optional.of(mockUser));




        EntryService entryService = new EntryService(mockEntryRepo, null,  userMockRepository);
        ArgumentCaptor<EntryEntity> entryEntityCaptor = ArgumentCaptor.forClass(EntryEntity.class);

        //Act
        entryService.createEntry(createEntryRequest);


        //Assert
        verify(mockEntryRepo, times(1)).save(any());
        verify(mockEntryRepo).save(entryEntityCaptor.capture());


        EntryEntity capturedEntry = entryEntityCaptor.getValue();
        assertEquals(1L, capturedEntry.getEntryUser().getId());
        assertEquals("Test content", capturedEntry.getContent());

    }

    @ParameterizedTest
    @MethodSource("provideForCreateEntry_ShouldThrowExceptionWhenInvalidParametersAreSupplied")
    void createEntry_ShouldThrowExceptionWhenInvalidParametersAreSupplied(Long userId, String content){
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(userId, content);
        EntryRepository mockRepo = mock(EntryRepository.class);
        UserRepository mockUerRepo = mock(UserRepository.class);

        EntryService entryService = new EntryService(mockRepo, null, mockUerRepo);

        assertThrows(InvalidParametersSuppliedException.class, () -> entryService.createEntry(createEntryRequest));
        verify(mockRepo, never()).save(any());
    }

    @Test
    void createEntry_ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        Long nonExistentUserId = 999L;
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(nonExistentUserId, "Some content");
        EntryRepository mockEntryRepo = mock(EntryRepository.class);
        UserRepository mockUserRepo = mock(UserRepository.class);

        // Simulate user not found
        when(mockUserRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntryService entryService = new EntryService(mockEntryRepo, null, mockUserRepo);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> entryService.createEntry(createEntryRequest));
    }

    @Test
    void deleteEntry_ShouldDeleteEntryById() {

    }



    private static Stream<Arguments> provideForCreateEntry_ShouldThrowExceptionWhenInvalidParametersAreSupplied(){
        return Stream.of(
            Arguments.of(null, null),
                    Arguments.of(0L, ""),
                    Arguments.of(0L, " ")
        );
    }
}
