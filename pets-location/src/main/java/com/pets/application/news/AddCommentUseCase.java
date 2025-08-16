package com.pets.application.news;

import com.pets.domain.model.Comment;
import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.User;
import com.pets.domain.repository.NewsRepository;
import com.pets.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AddCommentUseCase {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public AddCommentUseCase(
            NewsRepository newsRepository,
            UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    public NewsPost execute(Long postId, UUID authorId, String content) {
        NewsPost post = newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthorUserDto author = new AuthorUserDto(user.getId(), user.getName());

        post.getComments().add(new Comment(
                null,
                author,
                content,
                LocalDateTime.now()
        ));

        return newsRepository.save(post);
    }
}
