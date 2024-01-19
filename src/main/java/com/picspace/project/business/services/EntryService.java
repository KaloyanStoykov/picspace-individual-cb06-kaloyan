package com.picspace.project.business.services;

import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.exception.EntryNotFoundException;
import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.exception.PermissionDeniedException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.configuration.JwtService;
import com.picspace.project.configuration.SecurityConfig;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.*;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
@Builder
public class EntryService {
    private final EntryRepository entryRepo;
    private final EntryConverter entryConverter;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public CreateEntryResponse createEntry(CreateEntryRequest request){
        Optional<UserEntity> userEntity = userRepository.findById(request.getUserId());



        if( request.getContent() == null || request.getContent().isBlank()){
            throw new InvalidParametersSuppliedException("Content is invalid!");
        }
        if(request.getUserId() == null ||  request.getUserId() == 0L){
            throw new InvalidParametersSuppliedException("Invalid userId was supplied");
        }


        if(userEntity.isPresent()){
            EntryEntity entryEntity = EntryEntity.builder().entryUser(userEntity.get()).content(request.getContent()).dateCreated(new Date()).build();

            entryRepo.save(entryEntity);
            return CreateEntryResponse.builder().postId(entryEntity.getId()).message("Entry created successfully!").userId(userEntity.get().getId()).build();

        }


        throw new UserNotFoundException();

    }

    public GetEntriesByUserIdResponse getByUserId(Long userId) {
        if(userRepository.findById(userId).isEmpty()){
            throw new UserNotFoundException();
        }

        List<Entry> allUserEntries = new ArrayList<>();
        for(EntryEntity entryEntity: this.entryRepo.findByUserId(userId)){
            allUserEntries.add(entryConverter.toPojo(entryEntity));
        }

        return GetEntriesByUserIdResponse.builder().allUserEntries(allUserEntries).build();

    }

    public GetAllEntriesResponse getAllEntries(){
        List<Entry> allUserEntries = new ArrayList<>();
        for(EntryEntity entryEntity: this.entryRepo.findAll()){
            allUserEntries.add(entryConverter.toPojo(entryEntity));
        }

        return GetAllEntriesResponse.builder().allEntries(allUserEntries).build();
    }


    public DeleteEntryResponse deleteEntry(Long entryId){
        EntryEntity entryEntity = entryRepo.findById(entryId).orElseThrow(EntryNotFoundException::new);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        Long currentUserId = userEntity.getId();


        boolean isAdmin = userEntity.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

        if(isAdmin || Objects.equals(entryEntity.getEntryUser().getId(), currentUserId)){
            entryRepo.deleteById(entryId);

            return DeleteEntryResponse.builder().message("Entry Deleted successfully").build();
        }

        throw new PermissionDeniedException();



    }

    public UpdateEntryResponse updateEntry(Long entryId, String newContent, Long userId){
        return entryRepo.findById(entryId).map(entryEntity -> {
            if(!Objects.equals(entryEntity.getEntryUser().getId(), userId)){
                throw new PermissionDeniedException();
            }
            entryEntity.setContent(newContent);
            entryRepo.save(entryEntity);
            UpdateEntryResponse response = UpdateEntryResponse.builder().message("Entry updated successfully!").build();

            return response;
        }).orElseThrow(EntryNotFoundException::new);


    }

    public GetCountOfEntriesResponse countEntries(){
        return GetCountOfEntriesResponse.builder().countOfEntries(entryRepo.countTotalEntries()).build();
    }

}
