package com.pets.domain.repository;

import com.pets.domain.model.NewsPost;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository {
    List<NewsPost> findAll();
    Optional<NewsPost> findById(Long id);
    NewsPost save(NewsPost post);
    List<NewsPost> findByUserId(UUID userId);
    void deleteById(Long id);
}
