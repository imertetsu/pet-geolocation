package com.pets.infrastructure.mapper;

import com.pets.domain.model.Comment;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.Reaction;
import com.pets.domain.model.User;
import com.pets.infrastructure.controllers.dto.NewsCommentDto;
import com.pets.infrastructure.controllers.dto.NewsPostDto;
import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import com.pets.infrastructure.persistence.entities.NewsCommentEntity;
import com.pets.infrastructure.persistence.entities.NewsPostEntity;
import com.pets.infrastructure.persistence.entities.NewsReactionEntity;
import com.pets.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsMapper {

    public NewsPost toDomain(NewsPostEntity entity) {

        // Mapear comentarios
        List<Comment> comments = entity.getComments().stream()
                .map(comment -> new Comment(
                        comment.getId(),
                        new AuthorUserDto(comment.getAuthor().getId(), comment.getAuthor().getName()), // Autor como User
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        // Mapear reacciones
        List<Reaction> reactions = entity.getReactions().stream()
                .map(reaction -> new Reaction(
                        reaction.getId(),
                        reaction.getUserId(),
                        reaction.getReactionType()
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        // Mapear el post
        return new NewsPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCategory(),
                entity.getCreatedAt(),
                new User(entity.getAuthor().getId(), entity.getAuthor().getName(), null, null, null, null, null, null, null, null, null), // Autor como User
                entity.getCountry(),
                entity.getCity(),
                entity.getImages(),
                comments,
                reactions
        );
    }

    public NewsPostEntity toEntity(NewsPost post, UserEntity authorEntity) {
        NewsPostEntity entity = new NewsPostEntity();
        entity.setId(post.getId());
        entity.setTitle(post.getTitle());
        entity.setContent(post.getContent());
        entity.setCategory(post.getCategory());
        entity.setCreatedAt(post.getCreatedAt());

        // Asignamos la relación ManyToOne
        entity.setAuthor(authorEntity);

        entity.setCountry(post.getCountry());
        entity.setCity(post.getCity());
        entity.setImages(post.getImages());

        // Mapear comentarios
        List<NewsCommentEntity> commentEntities = post.getComments().stream()
                .map(comment -> {
                    NewsCommentEntity c = new NewsCommentEntity();
                    c.setId(comment.getId());
                    // Aquí se asume que tienes un UserEntity para cada autor del comentario
                    UserEntity commentAuthor = new UserEntity();
                    commentAuthor.setId(comment.getAuthor().getId());
                    commentAuthor.setName(comment.getAuthor().getName());
                    c.setAuthor(commentAuthor);

                    c.setContent(comment.getContent());
                    c.setCreatedAt(comment.getCreatedAt());
                    c.setNewsPost(entity);
                    return c;
                })
                .collect(Collectors.toList());

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
                })
                .collect(Collectors.toList());

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

        // Autor del post
        dto.author = new AuthorUserDto();
        dto.author.setId(post.getAuthor().getId());
        dto.author.setName(post.getAuthor().getName());

        dto.country = post.getCountry();
        dto.city = post.getCity();
        dto.images = post.getImages();

        // Mapear comentarios
        dto.comments = post.getComments().stream().map(comment -> {
            NewsCommentDto c = new NewsCommentDto();
            c.id = comment.getId();

            // Autor del comentario
            c.author = new AuthorUserDto();
            c.author.setId(comment.getAuthor().getId());
            c.author.setName(comment.getAuthor().getName());

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

