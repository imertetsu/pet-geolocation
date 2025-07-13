package com.pets.application.news;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetNewsPostsUseCase {

    private final NewsRepository newsRepository;

    public GetNewsPostsUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<NewsPost> execute(NewsCategory category, LocalDate fromDate) {
        return newsRepository.findAll().stream()
                .filter(post -> category == null || post.getCategory() == category)
                .filter(post -> fromDate == null || !post.getCreatedAt().toLocalDate().isBefore(fromDate))
                .collect(Collectors.toList());
    }
}
