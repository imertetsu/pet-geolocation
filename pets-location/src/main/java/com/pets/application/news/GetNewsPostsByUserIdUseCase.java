package com.pets.application.news;

import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetNewsPostsByUserIdUseCase {

    private final NewsRepository newsRepository;

    public GetNewsPostsByUserIdUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<NewsPost> execute(UUID userId) {
        return newsRepository.findByUserId(userId);
    }
}
