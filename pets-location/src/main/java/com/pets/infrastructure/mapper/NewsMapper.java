package com.pets.infrastructure.mapper;

import com.pets.domain.model.Comment;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.Reaction;
import com.pets.infrastructure.controllers.dto.NewsCommentDto;
import com.pets.infrastructure.controllers.dto.NewsPostDto;
import com.pets.infrastructure.controllers.dto.UserDto;
import com.pets.infrastructure.persistence.entities.NewsCommentEntity;
import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NewsMapper {

    public NewsPost toDomain(NewsPostEntity entity) {
        List<Comment> comments = entity.getComments().stream().map(comment -> new Comment(
                comment.getId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getContent(),
                comment.getCreatedAt()
        )).collect(Collectors.toCollection(ArrayList::new));

        List<Reaction> reactions = entity.getReactions().stream().map(reaction ->
                new Reaction(
                        reaction.getId(),
                        reaction.getUserId(),
                        reaction.getReactionType()
                )
        ).collect(Collectors.toCollection(ArrayList::new));

        return new NewsPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getAuthorId(),
                entity.getAuthorName(),
                entity.getImages(),
                comments,
                reactions
        );
    }

    public NewsPostEntity toEntity(NewsPost post) {
        NewsPostEntity entity = new NewsPostEntity();
        entity.setId(post.getId());
        entity.setTitle(post.getTitle());
        entity.setContent(post.getContent());
        entity.setCategory(post.getCategory());
        entity.setCreatedAt(post.getCreatedAt());
        entity.setAuthorId(post.getAuthorId());
        entity.setAuthorName(post.getAuthorName());
        entity.setImages(post.getImages());

        // Mapear comentarios
        List<NewsCommentEntity> commentEntities = post.getComments().stream()
                .map(comment -> {
                    NewsCommentEntity c = new NewsCommentEntity();
                    c.setId(comment.getId());
                    c.setAuthorId(comment.getAuthorId());
                    c.setAuthorName(comment.getAuthorName());
                    c.setContent(comment.getContent());
                    c.setCreatedAt(comment.getCreatedAt());
                    c.setNewsPost(entity);
                    return c;
                }).collect(Collectors.toList());

        entity.setComments(commentEntities);

        // Mapear reacciones
        List<NewsReactionEntity> reactionEntities = post.getReactions().stream()
                .map(reaction -> {
                    NewsReactionEntity r = new NewsReactionEntity();
                    r.setId(reaction.getId());
                    r.setUserId(reaction.getUserId());
                    r.setReactionType(reaction.getType());
                    r.setNewsPost(entity);
                    return r;
                }).collect(Collectors.toList());

        entity.setReactions(reactionEntities);

        return entity;
    }

    public NewsPostDto toDto(NewsPost post) {
        NewsPostDto dto = new NewsPostDto();
        dto.id = post.getId();
        dto.title = post.getTitle();
        dto.content = post.getContent();
        dto.category = post.getCategory();
        dto.createdAt = post.getCreatedAt();

        dto.author = new UserDto();
        dto.author.id = post.getAuthorId();
        dto.author.name = post.getAuthorName();

        dto.images = post.getImages();

        // Mapear comentarios
        dto.comments = post.getComments().stream().map(comment -> {
            NewsCommentDto c = new NewsCommentDto();
            c.id = comment.getId();
            c.authorId = comment.getAuthorId();
            c.authorName = comment.getAuthorName();
            c.content = comment.getContent();
            c.createdAt = comment.getCreatedAt();
            return c;
        }).toList();

        // Agrupar reacciones por tipo y contarlas
        dto.reactions = post.getReactions().stream()
                .collect(Collectors.groupingBy(
                        Reaction::getType,
                        Collectors.counting()
                ));
        return dto;
    }
}

