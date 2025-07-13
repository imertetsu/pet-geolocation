package com.pets.application.news;

import com.pets.domain.repository.NewsReactionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteReactionUseCase {

    private final NewsReactionRepository reactionRepository;

    public DeleteReactionUseCase(NewsReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public void execute(Long postId, UUID userId) {
        boolean exists = reactionRepository.findByPostIdAndUserId(postId, userId).isPresent();

        if (!exists) {
            throw new RuntimeException("No existe reacción para eliminar");
        }

        reactionRepository.deleteByPostIdAndUserId(postId, userId);
    }
}

