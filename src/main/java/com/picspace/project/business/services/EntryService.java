package com.picspace.project.business.services;

import com.picspace.project.business.dbConverter.EntryConverter;
import com.picspace.project.business.exception.EntryNotFoundException;
import com.picspace.project.business.exception.InvalidParametersSuppliedException;
import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryRequest;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryResponse;
import com.picspace.project.domain.restRequestResponse.entryREST.DeleteEntryResponse;
import com.picspace.project.domain.restRequestResponse.entryREST.GetEntriesByUserIdResponse;
import com.picspace.project.persistence.EntryRepository;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Builder
public class EntryService {
    private final EntryRepository entryRepo;
    private final EntryConverter entryConverter;
    private final UserRepository userRepository;


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
            return CreateEntryResponse.builder().postId(entryEntity.getId()).message("Entry created successfully!").build();

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

    public DeleteEntryResponse deleteEntry(Long entryId){
        if(entryRepo.findById(entryId).isEmpty()){
            throw new EntryNotFoundException();
        }
        entryRepo.deleteById(entryId);
        return DeleteEntryResponse.builder().message("Entry deleted successfully").build();
    }

}
