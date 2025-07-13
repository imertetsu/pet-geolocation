package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.repository.NewsReactionRepository;
import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class NewsReactionRepositoryImpl implements NewsReactionRepository {

    private final NewsReactionJpaRepository jpaRepository;

    public NewsReactionRepositoryImpl(NewsReactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<NewsReactionEntity> findByPostIdAndUserId(Long postId, UUID userId) {
        return jpaRepository.findByNewsPostIdAndUserId(postId, userId);
    }

    @Override
    public NewsReactionEntity save(NewsReactionEntity reaction) {
        return jpaRepository.save(reaction);
    }
    @Transactional
    @Override
    public void deleteByPostIdAndUserId(Long postId, UUID userId) {
        jpaRepository.deleteByNewsPost_IdAndUserId(postId, userId);
    }
}

