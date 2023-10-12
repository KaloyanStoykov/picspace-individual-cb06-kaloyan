package com.picspace.project.persistence;

import com.picspace.project.domain.Entry;

import java.util.List;

public interface EntryRepository {
    void saveEntry(Entry entry);

    List<Entry> getByUserId(Integer id);

}
