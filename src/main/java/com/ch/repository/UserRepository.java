package com.ch.repository;

import com.ch.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}
