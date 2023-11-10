package com.picspace.project.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Length(min = 2, max = 20)
    private String name;

    @NotBlank
    @Column(name = "last_name")
    @Length(min = 2, max = 30)
    private String lastName;

    @NotBlank
    @Column(name = "username")
    @Length(min = 2, max = 30)
    private String username;

    @NotNull
    @Column(name = "password")
    @Length(min = 2, max = 30)
    @ToString.Exclude
    private String password;

    @NotNull
    @Range(min = 18, max = 110)
    @Column(name = "age")
    private int age;

    @NotNull
    @Column(name = "registered_at")
    @NotNull
    private LocalDateTime registeredAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="entries",
            joinColumns = @JoinColumn(name= "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")

    )
    private List<EntryEntity> userEntries;



}
