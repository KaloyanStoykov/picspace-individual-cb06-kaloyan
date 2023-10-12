package com.picspace.project;


import com.picspace.project.business.services.EntryService;

import com.picspace.project.domain.Entry;
import com.picspace.project.persistence.impl.FakeEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntryServiceTest {

    private EntryService entryService;
    @Test
    public void createPost_ShouldCreatePost_InRepo(){
        Entry newEntry = new Entry();
        newEntry.setUserId(1);
        newEntry.setContent("Testing content");
        newEntry.setDateCreated(LocalDateTime.now());
        entryService.createEntry(newEntry);


        List<Entry> userEntries = entryService.getByUserId(1);

        assertEquals(2, userEntries.size());
    }

    @Test
    public void getByUserId_ShouldReturnOne_FromRepo(){
        Integer userId = 1;

        Integer expectedEntriesCountFromRepo = 1;
        Integer actualCountEntries = entryService.getByUserId(userId).size();

        assertEquals(expectedEntriesCountFromRepo, actualCountEntries);
    }

    @BeforeEach
    public void setUpPostServiceTest(){
        entryService = new EntryService(new FakeEntryRepository());
    }
}
