package com.pets.infrastructure.controllers;

import org.springframework.data.domain.Page;
import com.pets.application.news.*;
import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.ReactionType;
import com.pets.infrastructure.controllers.dto.NewsCommentDto;
import com.pets.infrastructure.controllers.dto.NewsPostDto;
import com.pets.infrastructure.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final GetNewsPostByIdUseCase getNewsPostByIdUseCase;
    private final GetNewsPostsByUserIdUseCase getNewsPostsByUserIdUseCase;
    private final UpdateNewsPostUseCase updateNewsPostUseCase;

    // 1. Obtener publicaciones (con filtros opcionales)
    @GetMapping
    public Page<NewsPostDto> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        NewsCategory categoryEnum = null;
        if (category != null) {
            try {
                categoryEnum = NewsCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría inválida");
            }
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Convertir LocalDate a LocalDateTime al inicio del día
        LocalDateTime fromDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;

        Page<NewsPost> postsPage = getNewsPosts.execute(categoryEnum, fromDateTime, country, city, pageRequest);

        Page<NewsPostDto> dtoPage = postsPage.map(post -> {
            NewsPostDto dto = mapper.toDto(post);
            if (userId != null) {
                getUserReactionUseCase.execute(post.getId(), userId)
                        .ifPresent(rt -> dto.setUserReaction(rt.name()));
            }
            return dto;
        });

        return dtoPage;
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
                request.country,
                request.city,
                request.images
        );
        return mapper.toDto(created);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<NewsPostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody NewsPostUpdateRequest request) {

        NewsPost updatedPost = updateNewsPostUseCase.execute(
                postId,
                request.title(),
                request.content(),
                request.category(),
                request.country(),
                request.city(),
                request.images()
        );

        return ResponseEntity.ok(mapper.toDto(updatedPost));
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

    @GetMapping("/{postId}")
    public ResponseEntity<NewsPost> getNewsPostById(@PathVariable Long postId) {
        try {
            NewsPost post = getNewsPostByIdUseCase.execute(postId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public List<NewsPostDto> getNewsByUserId(@PathVariable UUID userId) {
        List<NewsPost> posts = getNewsPostsByUserIdUseCase.execute(userId);

        return posts.stream()
                .map(post -> {
                    NewsPostDto dto = mapper.toDto(post);

                    // Si quieres agregar la reacción del usuario, asumiendo que es el mismo userId del path:
                    getUserReactionUseCase.execute(post.getId(), userId)
                            .ifPresent(rt -> dto.setUserReaction(rt.name()));

                    return dto;
                })
                .toList();
    }
    // DTO auxiliar para crear publicaciones
    public static class CreateNewsPostRequest {
        public String title;
        public String content;
        public NewsCategory category;
        public UUID authorId;
        public String authorName;
        public String country;
        public String city;
        public List<String> images;
    }

    public record NewsPostUpdateRequest (
            String title,
            String content,
            NewsCategory category,
            String country,
            String city,
            List<String> images
    ){}
}
