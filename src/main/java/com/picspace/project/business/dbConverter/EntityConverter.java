package com.picspace.project.business.dbConverter;

public interface EntityConverter<Entity, Pojo> {
    Entity toEntity(Pojo pojo);
    Pojo toPojo(Entity entity);
}
