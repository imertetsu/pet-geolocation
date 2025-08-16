package com.pets.application.news;

import com.pets.domain.model.*;
import com.pets.domain.repository.NewsRepository;
import com.pets.domain.repository.UserRepository;
import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class CreateNewsPostUseCase {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public CreateNewsPostUseCase(
            NewsRepository newsRepository,
            UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    public NewsPost execute(String title, String content, NewsCategory category,
                            UUID authorId, String country, String city, List<String> images) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NewsPost post = new NewsPost(
                null,
                title,
                content,
                category,
                LocalDateTime.now(),
                author, // Ahora guardamos la relación
                country,
                city,
                images != null ? images : new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        return newsRepository.save(post);
    }
}
