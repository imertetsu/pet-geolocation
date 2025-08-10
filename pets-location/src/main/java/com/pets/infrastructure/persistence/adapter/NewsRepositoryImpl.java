package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import com.pets.infrastructure.mapper.NewsMapper;
import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

    private final NewsPostJpaRepository jpaRepository;
    private final NewsMapper mapper;

    @Override
    public List<NewsPost> findAll() {
        return jpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public Optional<NewsPost> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public NewsPost save(NewsPost post) {
        NewsPostEntity entity = mapper.toEntity(post);
        return mapper.toDomain(jpaRepository.save(entity));
    }
    @Override
    public List<NewsPost> findByUserId(UUID userId) {
        return jpaRepository.findByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
