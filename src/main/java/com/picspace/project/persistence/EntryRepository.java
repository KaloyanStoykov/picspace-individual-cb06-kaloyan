package com.picspace.project.persistence;

import com.picspace.project.persistence.entity.EntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EntryRepository extends JpaRepository<EntryEntity, Long> {

    @Query("SELECT e FROM EntryEntity e where e.entryUser.id = :userId")
    List<EntryEntity> findByUserId(Long userId);
}
