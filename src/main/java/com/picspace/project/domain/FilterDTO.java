package com.picspace.project.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FilterDTO {
    // Match the field name of the entity
    private String columnName;
    // Match the value in the database
    private Object columnValue;
}
