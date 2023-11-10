package com.picspace.project.persistence;

import com.picspace.project.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {




}
