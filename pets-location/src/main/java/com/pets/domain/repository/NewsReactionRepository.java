package com.pets.domain.repository;

import com.pets.infrastructure.persistence.entities.NewsReactionEntity;

import java.util.Optional;
import java.util.UUID;

public interface NewsReactionRepository {
    Optional<NewsReactionEntity> findByPostIdAndUserId(Long postId, UUID userId);
    NewsReactionEntity save(NewsReactionEntity reaction);
    void deleteByPostIdAndUserId(Long postId, UUID userId);
}

