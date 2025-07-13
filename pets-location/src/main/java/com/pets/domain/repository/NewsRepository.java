package com.pets.domain.repository;

import com.pets.domain.model.NewsPost;

import java.util.List;
import java.util.Optional;

public interface NewsRepository {
    List<NewsPost> findAll();
    Optional<NewsPost> findById(Long id);
    NewsPost save(NewsPost post);
    void deleteById(Long id);
}
