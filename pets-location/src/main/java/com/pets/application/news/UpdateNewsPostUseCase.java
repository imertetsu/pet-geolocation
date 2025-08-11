package com.pets.application.news;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateNewsPostUseCase {

    private final NewsRepository newsRepository;

    public UpdateNewsPostUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost execute(Long postId, String title, String content, NewsCategory category,
                            String country, String city, List<String> images) {

        NewsPost existingPost = newsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        // Actualizar campos
        existingPost.setTitle(title);
        existingPost.setContent(content);
        existingPost.setCategory(category);
        existingPost.setCountry(country);
        existingPost.setCity(city);
        existingPost.setImages(images != null ? images : new ArrayList<>());

        return newsRepository.save(existingPost);
    }
}

