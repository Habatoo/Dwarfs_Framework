package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findById(Integer tag_id);
    Optional<Tag> findByTagName(ETag tagName);

    Boolean existsByTagName(ETag tagName);
}
