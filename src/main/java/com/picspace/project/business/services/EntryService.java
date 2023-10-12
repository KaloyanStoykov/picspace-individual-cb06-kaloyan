package com.picspace.project.business.services;

import com.picspace.project.domain.Entry;
import com.picspace.project.persistence.EntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EntryService {
    private final EntryRepository entryRepo;

    public EntryService(EntryRepository entryRepo){
        this.entryRepo = entryRepo;
    }

    public void createEntry(Entry entry){
        this.entryRepo.saveEntry(entry);
    }

    public List<Entry> getByUserId(Integer userId) { return this.entryRepo.getByUserId(userId); }

}
