package com.pets.application.news;

import com.pets.domain.model.Comment;
import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AddCommentUseCase {

    private final NewsRepository newsRepository;

    public AddCommentUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(Long postId, UUID authorId, String authorName, String content) {
        NewsPost post = newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.getComments().add(new Comment(null, authorId, authorName, content, LocalDateTime.now()));

        return newsRepository.save(post);
    }
}
