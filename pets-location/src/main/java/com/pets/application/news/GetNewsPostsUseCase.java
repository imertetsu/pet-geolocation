package com.pets.application.news;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GetNewsPostsUseCase {

    private final NewsRepository newsRepository;

    public GetNewsPostsUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public Page<NewsPost> execute(
            NewsCategory category,
            LocalDateTime fromDate,
            String country,
            String city,
            Pageable pageable
    ) {
        return newsRepository.findByFilters(category, fromDate, country, city, pageable);
    }
}
