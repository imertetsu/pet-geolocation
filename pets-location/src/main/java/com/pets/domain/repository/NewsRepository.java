package com.pets.domain.repository;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository {
    List<NewsPost> findAll();
    Optional<NewsPost> findById(Long id);
    NewsPost save(NewsPost post);
    List<NewsPost> findByUserId(UUID userId);
    void deleteById(Long id);
    Page<NewsPost> findByFilters(
            NewsCategory category,
            LocalDateTime fromDate,
            String country,
            String city,
            Pageable pageable
    );
}
