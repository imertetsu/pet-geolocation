package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.NewsPost;
import com.pets.domain.repository.NewsRepository;
import com.pets.infrastructure.mapper.NewsMapper;
import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

    private final NewsPostJpaRepository jpaRepository;
    private final NewsMapper mapper;

    @Override
    public List<NewsPost> findAll() {
        return jpaRepository.findAll()
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
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
