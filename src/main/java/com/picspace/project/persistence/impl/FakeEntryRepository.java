package com.picspace.project.persistence.impl;

import com.picspace.project.business.services.UserService;
import com.picspace.project.domain.Entry;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.EntryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FakeEntryRepository implements EntryRepository {

    private static int NEXT_POST_ID = 2;

    private final List<Entry> savedEntries;
    private UserService userService;
    private User activeUser;
    public FakeEntryRepository() {
        savedEntries = new ArrayList<>();
        userService = new UserService(new FakeUserRepoImpl());
        activeUser = userService.getByUserId(1);

        savedEntries.add(new Entry(1, activeUser.getId(), "Test content", LocalDateTime.now()));
    }
    @Override
    public void saveEntry(Entry entry) {
        entry.setPostId(NEXT_POST_ID);
        savedEntries.add(entry);
        NEXT_POST_ID++;
    }

    @Override
    public List<Entry> getByUserId(Integer userId){
        return savedEntries.stream().filter(entry -> entry.getUserId().equals(userId)).toList();
    }

}
