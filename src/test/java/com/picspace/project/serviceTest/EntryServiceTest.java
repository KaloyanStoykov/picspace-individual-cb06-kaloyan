package com.picspace.project.serviceTest;


import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.services.EntryService;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryRequest;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EntryServiceTest {








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
