package com.picspace.project.business.dbConverter;


import com.picspace.project.domain.Entry;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.entity.EntryEntity;
import com.picspace.project.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class EntryConverter implements EntityConverter<EntryEntity, Entry> {
    private EntityConverter<UserEntity, User> userConverter;


    public EntryConverter(EntityConverter<UserEntity, User> userConverter) {
        this.userConverter = userConverter;
    }
    @Override
    public EntryEntity toEntity(Entry entry){
        return EntryEntity.builder()
                .entryUser(userConverter.toEntity(entry.getUser()))
                .content(entry.getContent())
                .dateCreated(entry.getDateCreated())
                .build();
    }

    @Override
    public Entry toPojo(EntryEntity entryEntity){
        return Entry.builder()
                .postId(entryEntity.getId())
                .user(userConverter.toPojo(entryEntity.getEntryUser()))
                .content(entryEntity.getContent())
                .dateCreated(entryEntity.getDateCreated())
                .build();
    }
}
