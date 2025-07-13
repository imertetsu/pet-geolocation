package com.pets.infrastructure.controllers;

import com.pets.application.news.*;
import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.ReactionType;
import com.pets.infrastructure.controllers.dto.NewsCommentDto;
import com.pets.infrastructure.controllers.dto.NewsPostDto;
import com.pets.infrastructure.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final GetNewsPostsUseCase getNewsPosts;
    private final CreateNewsPostUseCase createNewsPost;
    private final AddCommentUseCase addComment;
    private final ReactToPostUseCase reactToPost;
    private final NewsMapper mapper;
    private final UpdateReactionUseCase updateReactionUseCase;
    private final DeleteReactionUseCase deleteReactionUseCase;
    private final GetUserReactionUseCase getUserReactionUseCase;
    private final DeleteNewsPostUseCase deleteNewsPostUseCase;

    // 1. Obtener publicaciones (con filtros opcionales)
    @GetMapping
    public List<NewsPostDto> getAll(
            @RequestParam(required = false) NewsCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) UUID userId
    ) {
        List<NewsPostDto> newsPostDto = getNewsPosts.execute(category, fromDate)
                .stream()
                .map(post -> mapper.toDto(post))
                .toList();

        if (userId != null) {
            newsPostDto.forEach(dto -> {
                Optional<ReactionType> reaction = getUserReactionUseCase.execute(dto.getId(), userId);
                reaction.ifPresent(rt -> dto.setUserReaction(rt.name()));
            });
        }
        return newsPostDto;
    }

    // 2. Crear una publicación
    @PostMapping
    public NewsPostDto create(@RequestBody CreateNewsPostRequest request) {
        NewsPost created = createNewsPost.execute(
                request.title,
                request.content,
                request.category,
                request.authorId,
                request.authorName,
                request.images
        );
        return mapper.toDto(created);
    }

    // 3. Comentar una publicación
    @PostMapping("/{postId}/comments")
    public NewsPostDto addComment(@PathVariable Long postId, @RequestBody NewsCommentDto comment) {
        NewsPost updated = addComment.execute(
                postId,
                comment.authorId,
                comment.authorName,
                comment.content
        );
        return mapper.toDto(updated);
    }

    // 4. Reaccionar a una publicación
    @PostMapping("/{postId}/reactions")
    public NewsPostDto reactToPost(
            @PathVariable Long postId,
            @RequestParam("userId") UUID userId,
            @RequestParam("reaction") ReactionType reaction
    ) {
        NewsPost updated = reactToPost.execute(postId, userId, reaction);
        return mapper.toDto(updated);
    }
    //Reaccion de un usuario especifico
    @PutMapping("/{postId}/reactions")
    public ResponseEntity<String> updateReaction(
            @PathVariable Long postId,
            @RequestParam UUID userId,
            @RequestParam ReactionType reactionType) {
        try {
            updateReactionUseCase.execute(postId, userId, reactionType);
            return ResponseEntity.ok("Reacción actualizada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{postId}/reactions")
    public ResponseEntity<String> deleteReaction(
            @PathVariable Long postId,
            @RequestParam UUID userId) {
        try {
            deleteReactionUseCase.execute(postId, userId);
            return ResponseEntity.ok("Reacción eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/{postId}/user-reaction")
    public ResponseEntity<String> getUserReaction(
            @PathVariable Long postId,
            @RequestParam UUID userId
    ) {
        Optional<ReactionType> reaction = getUserReactionUseCase.execute(postId, userId);
        return reaction
                .map(rt -> ResponseEntity.ok(rt.name()))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        deleteNewsPostUseCase.execute(postId);
        return ResponseEntity.noContent().build();
    }

    // DTO auxiliar para crear publicaciones
    public static class CreateNewsPostRequest {
        public String title;
        public String content;
        public NewsCategory category;
        public UUID authorId;
        public String authorName;
        public List<String> images;
    }
}
