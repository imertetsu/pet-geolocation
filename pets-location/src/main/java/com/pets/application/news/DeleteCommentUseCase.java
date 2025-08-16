package com.pets.application.news;

import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteCommentUseCase {

    private final NewsRepository newsRepository;

    public DeleteCommentUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(Long postId, Long commentId, UUID requesterId) {
        NewsPost post = newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean removed = post.getComments().removeIf(comment ->
                comment.getId().equals(commentId) &&
                        comment.getAuthor().getId().equals(requesterId) // optional: only allow owner
        );

        if (!removed) {
            throw new RuntimeException("Comment not found or not authorized to delete");
        }

        return newsRepository.save(post);
    }
}

