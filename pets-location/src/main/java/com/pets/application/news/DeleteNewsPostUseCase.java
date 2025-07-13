package com.pets.application.news;

import com.pets.domain.repository.NewsRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteNewsPostUseCase {

    private final NewsRepository newsRepository;

    public DeleteNewsPostUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void execute(Long postId) {
        boolean exists = newsRepository.findById(postId).isPresent();
        if (!exists) {
            throw new RuntimeException("News post not found with id: " + postId);
        }

        newsRepository.deleteById(postId);
    }
}
