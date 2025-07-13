package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsPostJpaRepository extends JpaRepository<NewsPostEntity, Long> {

}
