package com.picspace.project.persistence.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name= "entries")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity entryUser;

    @NotBlank
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "date_created")
    private Date dateCreated;
}
