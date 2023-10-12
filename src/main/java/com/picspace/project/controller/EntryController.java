package com.picspace.project.controller;


import com.picspace.project.business.services.EntryService;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryResponse;
import com.picspace.project.domain.restRequestResponse.entryREST.GetEntriesByUserIdResponse;
import com.picspace.project.persistence.impl.FakeEntryRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/entries")
public class EntryController {

    private EntryService entryService;
    @PostMapping()
    public ResponseEntity<CreateEntryResponse> createPost(@RequestBody @Valid Entry entry){
        entryService = new EntryService(new FakeEntryRepository());
        entryService.createEntry(entry);
        CreateEntryResponse createEntryResponse = new CreateEntryResponse(entry.getPostId(), "You have created a entry!");
        return ResponseEntity.status(HttpStatus.CREATED).body(createEntryResponse);
    }

    @GetMapping()
    public ResponseEntity<GetEntriesByUserIdResponse> getUserEntries(){
        GetEntriesByUserIdResponse responseBody = new GetEntriesByUserIdResponse(entryService.getByUserId(1));
        return ResponseEntity.ok(responseBody);
    }


}
