package com.pets.application.news;

import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

@Component
public class GetNewsPostByIdUseCase {

    private final NewsRepository newsRepository;

    public GetNewsPostByIdUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(Long postId) {
        return newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("News post not found with ID: " + postId));
    }
}