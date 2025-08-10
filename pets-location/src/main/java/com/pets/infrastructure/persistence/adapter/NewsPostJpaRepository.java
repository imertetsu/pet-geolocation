package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NewsPostJpaRepository extends JpaRepository<NewsPostEntity, Long> {

    List<NewsPostEntity> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
}
