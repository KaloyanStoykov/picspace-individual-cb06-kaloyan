package com.picspace.project.serviceTest;


import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.services.EntryService;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryRequest;
import com.picspace.project.domain.restRequestResponse.entryREST.GetEntriesByUserIdResponse;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EntryServiceTest {



    @Test
    void getEntriesByUserId_shouldReturnUserEntries(){
        Long userId = 1L;
        UserEntity neededUser = UserEntity.builder().id(userId).name("Kal").lastName("Stoykov").username("kiko").age(20).registeredAt(LocalDateTime.now()).userEntries(null).build();
        UserEntity differentUser = UserEntity.builder().id(2L).name("Kal").lastName("Jackson").username("kikojack").age(21).registeredAt(LocalDateTime.now()).userEntries(null).build();

        EntryRepository mockEntryRepo= mock(EntryRepository.class);
        EntryConverter entryConverter = mock(EntryConverter.class);



        List<EntryEntity> savedEntries = Arrays.asList(
            EntryEntity.builder().id(1L).entryUser(neededUser).dateCreated(new Date()).content("testContent1").build(),
            EntryEntity.builder().id(2L).entryUser(neededUser).dateCreated(new Date()).content("testContent2").build(),
            EntryEntity.builder().id(3L).entryUser(differentUser).dateCreated(new Date()).content("testContent3").build(),
            EntryEntity.builder().id(4L).entryUser(differentUser).dateCreated(new Date()).content("testContent4").build(),
            EntryEntity.builder().id(5L).entryUser(differentUser).dateCreated(new Date()).content("testContent5").build(),
            EntryEntity.builder().id(6L).entryUser(differentUser).dateCreated(new Date()).content("testContent6").build()
        );



        List<EntryEntity> userEntriesEntities = Arrays.asList(
                EntryEntity.builder().id(1L).entryUser(neededUser).dateCreated(new Date()).content("testContent1").build(),
                EntryEntity.builder().id(2L).entryUser(neededUser).dateCreated(new Date()).content("testContent2").build()

        );

        when(mockEntryRepo.findAll()).thenReturn(savedEntries);

        when(mockEntryRepo.findByUserId(userId)).thenReturn(userEntriesEntities);


        List<Entry> userEntries = new ArrayList<>();
        for(EntryEntity entryEntity: userEntriesEntities ){
            userEntries.add(entryConverter.toPojo(entryEntity));
        }

        GetEntriesByUserIdResponse expectedResponse = GetEntriesByUserIdResponse.builder().allUserEntries(userEntries).build();


        EntryService entryService = new EntryService(mockEntryRepo, entryConverter, null);

        GetEntriesByUserIdResponse actualResponse = entryService.getByUserId(userId);

        verify(mockEntryRepo, times(1)).findByUserId(userId);

        assertEquals(expectedResponse.getAllUserEntries(), actualResponse.getAllUserEntries());
        assertEquals(expectedResponse.getAllUserEntries().size(), actualResponse.getAllUserEntries().size());

        // Expecting two entries since expected response returns 2 entries in the test
        for(int i = 0; i < 2; i++){
            assertEquals(expectedResponse.getAllUserEntries().get(i), actualResponse.getAllUserEntries().get(i));
        }
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





    private static Stream<Arguments> provideForCreateEntry_ShouldThrowExceptionWhenInvalidParametersAreSupplied(){
        return Stream.of(
            Arguments.of(null, null),
                    Arguments.of(0L, ""),
                    Arguments.of(0L, " ")
        );
    }
}
