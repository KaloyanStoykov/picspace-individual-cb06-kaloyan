package com.picspace.project.serviceTest;


import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.exception.EntryNotFoundException;
import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.exception.PermissionDeniedException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.services.EntryService;
import com.picspace.project.configuration.JwtService;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.*;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication mockAuthentication;

    private EntryService entryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entryService = new EntryService(mockEntryRepo, mockEntryConverter, mockUserRepo, jwtService);

        SecurityContextHolder.setContext(mockSecurityContext);

    }


    @Test
    void getByUserId_UserFound_ShouldReturnEntries() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        EntryEntity entryEntity1 = new EntryEntity();
        EntryEntity entryEntity2 = new EntryEntity();
        Entry entry1 = new Entry();
        Entry entry2 = new Entry();

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
        Long nonExistentUserId = 99L;

        UserRepository mockUserRepo = mock(UserRepository.class);
        when(mockUserRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntryRepository mockEntryRepo = mock(EntryRepository.class);
        EntryConverter entryConverter = mock(EntryConverter.class);

        EntryService entryService = new EntryService(mockEntryRepo, entryConverter, mockUserRepo, null);

        assertThrows(UserNotFoundException.class, () -> {
            entryService.getByUserId(nonExistentUserId);
        });

        verify(mockEntryRepo, never()).findByUserId(nonExistentUserId);
    }

    @Test
    void createEntry_shouldCreateEntry(){
        //Arrange
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(1L, "Test content" );
        EntryRepository mockEntryRepo = mock(EntryRepository.class);



        Long userId = createEntryRequest.getUserId();


        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        UserRepository userMockRepository = mock(UserRepository.class);



        when(userMockRepository.findById(userId)).thenReturn(Optional.of(mockUser));




        EntryService entryService = new EntryService(mockEntryRepo, null,  userMockRepository, null);
        ArgumentCaptor<EntryEntity> entryEntityCaptor = ArgumentCaptor.forClass(EntryEntity.class);


        entryService.createEntry(createEntryRequest);



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

        EntryService entryService = new EntryService(mockRepo, null, mockUerRepo, null);

        assertThrows(InvalidParametersSuppliedException.class, () -> entryService.createEntry(createEntryRequest));
        verify(mockRepo, never()).save(any());
    }

    @Test
    void createEntry_ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        Long nonExistentUserId = 999L;
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(nonExistentUserId, "Some content");
        EntryRepository mockEntryRepo = mock(EntryRepository.class);
        UserRepository mockUserRepo = mock(UserRepository.class);


        when(mockUserRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntryService entryService = new EntryService(mockEntryRepo, null, mockUserRepo, null);


        assertThrows(UserNotFoundException.class, () -> entryService.createEntry(createEntryRequest));
    }



    @Test
    public void testUpdateEntry_Success() {
        Long entryId = 1L;
        String newContent = "Updated content";

        EntryEntity mockEntryEntity = new EntryEntity();
        mockEntryEntity.setContent("Original content");
        mockEntryEntity.setEntryUser(UserEntity.builder().id(1L).build()); // Set the user of the entry

        when(mockEntryRepo.findById(entryId)).thenReturn(Optional.of(mockEntryEntity));

        UpdateEntryResponse response = entryService.updateEntry(entryId, newContent, 1L); // Pass the userId as well

        assertEquals("Entry updated successfully!", response.getMessage());
        verify(mockEntryRepo).save(mockEntryEntity);
        assertEquals(newContent, mockEntryEntity.getContent());
    }

    @Test
    void testUpdateEntry_PermissionDenied() {
        Long entryId = 1L;
        String newContent = "Updated content";
        Long userId = 1L;
        Long wrongUserId = 2L;

        EntryEntity mockEntryEntity = mock(EntryEntity.class);
        when(mockEntryEntity.getEntryUser()).thenReturn(UserEntity.builder().id(userId).build());
        when(mockEntryRepo.findById(entryId)).thenReturn(Optional.of(mockEntryEntity));

        assertThrows(PermissionDeniedException.class, () -> {
            entryService.updateEntry(entryId, newContent, wrongUserId);
        });
    }

    @Test
    public void testUpdateEntry_EntryNotFound() {
        Long entryId = 1L;
        String newContent = "Updated content";
        Long userId = 1L;

        when(mockEntryRepo.findById(entryId)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class, () -> {
            entryService.updateEntry(entryId, newContent, userId);
        });
    }


    @Test
    public void deleteOwnEntry_ShouldSucceed() {
        UserEntity nonAdmin = new UserEntity();
        nonAdmin.setId(1L);
        nonAdmin.setRoles(Collections.singletonList(new RoleEntity(2L, "ROLE_USER")));

        EntryEntity entry = new EntryEntity();
        entry.setId(1L);
        entry.setEntryUser(nonAdmin);

        when(mockEntryRepo.findById(1L)).thenReturn(Optional.of(entry));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(nonAdmin);

        DeleteEntryResponse response = entryService.deleteEntry(1L);

        verify(mockEntryRepo).deleteById(1L);
        assertEquals("Entry Deleted successfully", response.getMessage());
    }

    @Test
    public void deleteOthersEntry_NonAdmin_ShouldFail() {
        UserEntity nonAdmin = new UserEntity();
        nonAdmin.setId(1L);
        nonAdmin.setRoles(Collections.singletonList(new RoleEntity(2L, "ROLE_USER")));

        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);

        EntryEntity entry = new EntryEntity();
        entry.setId(1L);
        entry.setEntryUser(otherUser);

        when(mockEntryRepo.findById(1L)).thenReturn(Optional.of(entry));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(nonAdmin);

        assertThrows(PermissionDeniedException.class, () -> entryService.deleteEntry(1L));
        verify(mockEntryRepo, never()).deleteById(1L);
    }


    @Test
    public void deleteAnyEntry_Admin_ShouldSucceed() {
        UserEntity admin = new UserEntity();
        admin.setId(1L);
        admin.setRoles(Collections.singletonList(new RoleEntity(2L, "ROLE_ADMIN")));

        EntryEntity entry = new EntryEntity();
        entry.setId(1L);
        entry.setEntryUser(new UserEntity()); // Entry created by another user

        when(mockEntryRepo.findById(1L)).thenReturn(Optional.of(entry));
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(admin);

        DeleteEntryResponse response = entryService.deleteEntry(1L);

        verify(mockEntryRepo).deleteById(1L);
        assertEquals("Entry Deleted successfully", response.getMessage());
    }

    @Test
    public void deleteNonExistentEntry_ShouldThrowException() {
        when(mockEntryRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class, () -> entryService.deleteEntry(1L));
        verify(mockEntryRepo, never()).deleteById(1L);
    }


    @Test
    public void getAllEntries_ShouldReturnAllEntries() {

        EntryEntity entryEntity1 = EntryEntity.builder().id(1L).content("Content 1").build();
        EntryEntity entryEntity2 = EntryEntity.builder().id(2L).content("Content 2").build();
        Entry pojo1 = new Entry();
        Entry pojo2 = new Entry();

        List<EntryEntity> entryEntities = Arrays.asList(entryEntity1, entryEntity2);

        when(mockEntryRepo.findAll()).thenReturn(entryEntities);
        when(mockEntryConverter.toPojo(entryEntity1)).thenReturn(pojo1);
        when(mockEntryConverter.toPojo(entryEntity2)).thenReturn(pojo2);


        GetAllEntriesResponse response = entryService.getAllEntries();


        List<Entry> actualEntries = response.getAllEntries();
        assertEquals(2, actualEntries.size());
        assertTrue(actualEntries.contains(pojo1));
        assertTrue(actualEntries.contains(pojo2));
        verify(mockEntryRepo).findAll();

    }

    @Test
    void testCountEntries() {

        Long expectedCount = 10L;
        when(mockEntryRepo.countTotalEntries()).thenReturn(expectedCount);


        GetCountOfEntriesResponse response = entryService.countEntries();

        assertEquals(expectedCount, response.getCountOfEntries(), "The count of entries should match the expected value");
    }


    private static Stream<Arguments> provideForCreateEntry_ShouldThrowExceptionWhenInvalidParametersAreSupplied(){
        return Stream.of(
            Arguments.of(null, null),
                    Arguments.of(0L, ""),
                    Arguments.of(0L, " ")
        );
    }
}
