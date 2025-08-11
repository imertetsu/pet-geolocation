package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.NewsCategory;
import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NewsPostJpaRepository extends JpaRepository<NewsPostEntity, Long> {

    List<NewsPostEntity> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
    @Query("""
    SELECT n FROM NewsPostEntity n
    WHERE (:category IS NULL OR n.category = :category)
      AND (:fromDate IS NULL OR n.createdAt >= :fromDate)
      AND (:country IS NULL OR LOWER(n.country) = LOWER(:country))
      AND (:city IS NULL OR LOWER(n.city) = LOWER(:city))
    """)
    Page<NewsPostEntity> findByFilters(
            @Param("category") NewsCategory category,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("country") String country,
            @Param("city") String city,
            Pageable pageable
    );

}
