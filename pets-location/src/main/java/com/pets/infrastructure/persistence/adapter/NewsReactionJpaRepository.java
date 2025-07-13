package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsReactionJpaRepository extends JpaRepository<NewsReactionEntity, Long> {
    Optional<NewsReactionEntity> findByNewsPost_IdAndUserId(Long postId, UUID userId);
    List<NewsReactionEntity> findAllByNewsPost_Id(Long postId);
    Optional<NewsReactionEntity> findByNewsPostIdAndUserId(Long postId, UUID userId);
    void deleteByNewsPost_IdAndUserId(Long postId, UUID userId);
}

