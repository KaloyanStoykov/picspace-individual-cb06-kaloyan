package com.picspace.project.controller;


import com.picspace.project.business.services.EntryService;
import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryRequest;
import com.picspace.project.domain.restRequestResponse.entryREST.CreateEntryResponse;
import com.picspace.project.domain.restRequestResponse.entryREST.GetEntriesByUserIdResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/entries")
public class EntryController {

    private EntryService entryService;
    private UserService userService;
    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CreateEntryResponse> createPost(@RequestBody CreateEntryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(entryService.createEntry(request));
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GetEntriesByUserIdResponse> getUserEntries(){

        return ResponseEntity.ok(entryService.getByUserId(userService.getByUserId(1L).getId()));
    }


}
