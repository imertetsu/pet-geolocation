package com.pets.application.news;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class CreateNewsPostUseCase {

    private final NewsRepository newsRepository;

    public CreateNewsPostUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(String title, String content, NewsCategory category,
                            UUID authorId, String authorName, String country, String city, List<String> images) {

        NewsPost post = new NewsPost(
                null,
                title,
                content,
                category,
                LocalDateTime.now(),
                authorId,
                authorName,
                country,
                city,
                images != null ? images : new ArrayList<>(),
                new ArrayList<>(), // comentarios
                new ArrayList<>()  // reacciones
        );

        return newsRepository.save(post);
    }
}
