package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findById(Long id);
    Optional<User> findByUserEmail(String userEmail);

    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);
}
