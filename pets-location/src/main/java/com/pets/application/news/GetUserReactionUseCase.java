package com.pets.application.news;

import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsReactionRepository;
import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class GetUserReactionUseCase {

    private final NewsReactionRepository reactionRepository;

    public GetUserReactionUseCase(NewsReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Optional<ReactionType> execute(Long postId, UUID userId) {
        return reactionRepository
                .findByPostIdAndUserId(postId, userId)
                .map(NewsReactionEntity::getReactionType);
    }
}

