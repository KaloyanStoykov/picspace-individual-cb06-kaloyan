package com.picspace.project.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entry {
    @ToString.Exclude
    private Integer postId;
    private Integer userId;
    private String content;
    private LocalDateTime dateCreated;
}
