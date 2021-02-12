package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findById(Long id);
    Token findByToken(String token);

    List<Token> findByExpiryDateBefore(LocalDateTime localDateTime);
    Boolean existsByToken(String token);
}
