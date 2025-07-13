package com.pets.application.news;

import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsReactionRepository;
import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateReactionUseCase {

    private final NewsReactionRepository reactionRepository;

    public UpdateReactionUseCase(NewsReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public void execute(Long postId, UUID userId, ReactionType newReactionType) {
        NewsReactionEntity reaction = reactionRepository
                .findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("No existe reacción previa para modificar"));

        reaction.setReactionType(newReactionType);
        reactionRepository.save(reaction);
    }
}
