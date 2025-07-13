package com.pets.application.news;

import com.pets.domain.model.NewsPost;
import com.pets.domain.model.Reaction;
import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReactToPostUseCase {

    private final NewsRepository newsRepository;

    public ReactToPostUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(Long postId, UUID userId, ReactionType reactionType) {
        NewsPost post = newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyReacted = post.getReactions().stream()
                .anyMatch(r -> r.getUserId().equals(userId));

        if (alreadyReacted) {
            throw new RuntimeException("El usuario ya reaccionó a este post");
        }

        // Agregar nueva reacción
        post.getReactions().add(new Reaction(null, userId, reactionType));

        return newsRepository.save(post);
    }
}
