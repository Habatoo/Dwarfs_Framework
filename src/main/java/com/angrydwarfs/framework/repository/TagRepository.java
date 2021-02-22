package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findById(Long id);
    Optional<Tag> findByTagName(ETag tagName);
    //Optional<Tag> findByTagName(String tagName);

    Boolean existsByTagName(ETag tagName);
}
