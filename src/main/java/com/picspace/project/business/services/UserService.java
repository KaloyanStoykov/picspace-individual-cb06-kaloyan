package com.picspace.project.business.services;


import com.picspace.project.business.dbConverter.UserConverter;
import com.picspace.project.business.exception.NoFilteredUsersFoundException;
import com.picspace.project.business.exception.PermissionDeniedException;
import com.picspace.project.business.exception.UserNotFoundException;

import com.picspace.project.domain.FilterDTO;
import com.picspace.project.domain.User;
import com.picspace.project.domain.restRequestResponse.userREST.*;
import com.picspace.project.persistence.UserRepository;
import com.picspace.project.persistence.UserSpecification;
import com.picspace.project.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserConverter userConverter;

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
            }
        };
    }



    public UserEntity save(UserEntity newUser){
        if(newUser.getId() == null){
            newUser.setRegisteredAt(LocalDateTime.now());
        }
        return userRepo.save(newUser);
    }


    public GetFilteredUsersResponse getFilteredUsers(List<FilterDTO> filterDTOList, int page, int size){
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<UserEntity> spec = UserSpecification.columnEqual(filterDTOList);

        Page<UserEntity> userPage = userRepo.findAll(spec, pageable);

        if (userPage.getContent().size() == 0) {
            throw new NoFilteredUsersFoundException();
        }

        List<User> allUsers = userPage.getContent().stream()
                .map(userConverter::toPojo)
                .collect(Collectors.toList());



        return GetFilteredUsersResponse.builder()
                .allUsers(allUsers)
                .currentPage(userPage.getNumber() + 1)
                .totalItems(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();

    }


    public GetUserByIdResponse getByUserId(Long id) {
        Optional<UserEntity> foundUser = userRepo.findById(id);



        if(foundUser.isPresent()){
            UserEntity userEntity = foundUser.get();
            return GetUserByIdResponse.builder().id(userEntity.getId()).name(userEntity.getName()).lastName(userEntity.getLastName()).age(userEntity.getAge()).username(userEntity.getUsername()).password(userEntity.getPassword()).registeredAt(userEntity.getRegisteredAt()).build();
        }

        throw new UserNotFoundException();
    }

    public DeleteUserResponse deleteUserById(Long userId){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        Long currentUserId = userEntity.getId();


        boolean isAdmin = userEntity.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

        if(isAdmin){
            if(userRepo.findById(userId).isPresent()){
                userRepo.deleteById(userId);
                return DeleteUserResponse.builder().message("User Deleted Successfully").build();
            }
            throw new UserNotFoundException();

        }

        throw new PermissionDeniedException();



    }




    public GetAllUsersResponse getAllUsers(int page, int size) {

        // Combine it with the specification to exclude admins
        Specification<UserEntity> spec = UserSpecification.excludeAdmins();

        // Fetch the page of users with the combined specifications
        Page<UserEntity> pageUserEntities = userRepo.findAll(spec, PageRequest.of(page, size));
        List<User> users = pageUserEntities.getContent().stream()
                .map(userConverter::toPojo)
                .collect(Collectors.toList());

        return GetAllUsersResponse.builder()
                .allUsers(users)
                .currentPage(pageUserEntities.getNumber() + 1) // Assuming page numbers are 1-based in the response
                .totalItems(pageUserEntities.getTotalElements())
                .totalPages(pageUserEntities.getTotalPages())
                .build();
    }

    public UpdateUserResponse updateUser(Long userId, String name, String lastName, String username, int age) {
        return userRepo.findById(userId)
                .map(user -> {
                    user.setName(name);
                    user.setLastName(lastName);
                    user.setUsername(username);
                    user.setAge(age);
                    UserEntity userEntity = userRepo.save(user);

                    UpdateUserResponse response = new UpdateUserResponse("User updated successfully!", userEntity.getId());

                    return response;


                })
                .orElseThrow(UserNotFoundException::new);
    }


}
