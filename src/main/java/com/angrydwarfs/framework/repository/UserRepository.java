package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    //Optional<User> findById(Long user_id);
    Optional<User> findByUserEmail(String userEmail);

    @EntityGraph(attributePaths = { "subscriptions", "subscribers" })
    Optional<User> findById(Long user_id);

    Optional<User> findBySocialNetId(String socialNetId);

    Boolean existsByUsername(String username);
    Boolean existsByUserEmail(String userEmail);
}
