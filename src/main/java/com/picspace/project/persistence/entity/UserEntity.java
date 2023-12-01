package com.picspace.project.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity implements UserDetails {

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


    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Collection<RoleEntity> roles;




    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="entries",
            joinColumns = @JoinColumn(name= "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")

    )
    private List<EntryEntity> userEntries;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getUsername(){
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
