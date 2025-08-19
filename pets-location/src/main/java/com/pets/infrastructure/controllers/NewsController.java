package com.pets.infrastructure.controllers;

import com.pets.domain.model.*;
import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import org.springframework.data.domain.Page;
import com.pets.application.news.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final DeleteCommentUseCase deleteCommentUseCase;

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
    public NewsPostDto addComment(@PathVariable Long postId, @RequestBody NewsCommentRequest comment) {
        NewsPost updated = addComment.execute(
                postId,
                comment.authorId,
                comment.content
        );
        return mapper.toDto(updated);
    }
    // Eliminar un comentario de una publicacion
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<NewsPost> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam UUID requesterId
    ) {
        NewsPost updatedPost = deleteCommentUseCase.execute(postId, commentId, requesterId);
        return ResponseEntity.ok(updatedPost);
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
    public ResponseEntity<NewsPostDto> getNewsPostById(@PathVariable Long postId) {
        try {
            NewsPost post = getNewsPostByIdUseCase.execute(postId);
            NewsPostDto dto = mapper.toDto(post);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(value = "/public/{postId}", produces = "text/html")
    public ResponseEntity<String> getPublicPostHtml(@PathVariable Long postId) {
        try {
            NewsPost post = getNewsPostByIdUseCase.execute(postId);

            String imageUrl = post.getImages() != null && !post.getImages().isEmpty()
                    ? post.getImages().get(0)
                    : "https://mi-dominio.com/default-image.jpg";

            // Conteos de reacciones
            Map<ReactionType, Long> reactionCounts = post.getReactions().stream()
                    .collect(Collectors.groupingBy(Reaction::getType, Collectors.counting()));

            int likeCount = reactionCounts.getOrDefault(ReactionType.LIKE, 0L).intValue();
            int loveCount = reactionCounts.getOrDefault(ReactionType.LOVE, 0L).intValue();
            int sadCount  = reactionCounts.getOrDefault(ReactionType.SAD, 0L).intValue();
            int hopeCount = reactionCounts.getOrDefault(ReactionType.HOPE, 0L).intValue();

            // Generar HTML para los comentarios
            String commentsHtml = post.getComments().isEmpty()
                    ? "<p style='color:grey;'>No hay comentarios aún.</p>"
                    : post.getComments().stream()
                    .map(c -> """
                        <div class="comment">
                          <div class="comment-author"><b>%s</b> <span class="date">%s</span></div>
                          <div class="comment-content">%s</div>
                        </div>
                        """.formatted(
                            c.getAuthor().getName(),
                            c.getCreatedAt().toLocalDate().toString(),
                            c.getContent()
                    ))
                    .collect(Collectors.joining());

            String html = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>%s</title>
          <meta property="og:title" content="%s"/>
          <meta property="og:description" content="%s"/>
          <meta property="og:image" content="%s"/>
          <meta property="og:type" content="article"/>
          <style>
            body {
              font-family: Arial, sans-serif;
              background: #f5f6f8;
              display: flex;
              justify-content: center;
              padding: 20px;
            }
            .card {
              background: white;
              border-radius: 12px;
              box-shadow: 0 2px 8px rgba(0,0,0,0.1);
              max-width: 600px;
              width: 100%%;
              overflow: hidden;
            }
            .header {
              display: flex;
              align-items: center;
              padding: 15px;
              border-bottom: 1px solid #eee;
            }
            .avatar {
              width: 40px;
              height: 40px;
              border-radius: 50%%;
              background: #ddd;
              display: flex;
              align-items: center;
              justify-content: center;
              margin-right: 10px;
            }
            .content {
              padding: 15px;
            }
            .reactions {
              display: flex;
              justify-content: space-around;
              padding: 10px 0;
              border-top: 1px solid #eee;
              color: #555;
            }
            .reaction {
              display: flex;
              align-items: center;
              gap: 5px;
              font-size: 14px;
            }
            .like { color: #1976d2; }
            .love { color: #e53935; }
            .sad  { color: #fb8c00; }
            .hope { color: #fbc02d; }
            .download {
              text-align: center;
              padding: 20px;
            }
            .btn {
              background:#1976d2;
              color:white;
              padding:12px 20px;
              border-radius:8px;
              text-decoration:none;
              font-weight:bold;
            }
            .comments {
              padding: 15px;
              border-top: 1px solid #eee;
            }
            .comment {
              margin-bottom: 12px;
              padding-bottom: 8px;
              border-bottom: 1px solid #f0f0f0;
            }
            .comment-author {
              font-size: 14px;
              color: #333;
            }
            .comment-author .date {
              font-size: 12px;
              color: grey;
              margin-left: 6px;
            }
            .comment-content {
              font-size: 14px;
              margin-top: 4px;
            }
          </style>
        </head>
        <body>
          <div class="card">
            <div class="header">
              <div class="avatar">👤</div>
              <div>
                <div><b>%s</b></div>
                <div style="font-size:12px; color:grey;">%s</div>
              </div>
            </div>
            <img src="%s" style="width:100%%; max-height: 300px; object-fit: cover;" alt="Imagen del post"/>
            <div class="content">
              <h2>%s</h2>
              <p>%s</p>
            </div>
            <div class="reactions">
              <div class="reaction like">👍 %d</div>
              <div class="reaction love">❤️ %d</div>
              <div class="reaction sad">😢 %d</div>
              <div class="reaction hope">⭐ %d</div>
            </div>
            <div class="comments">
              <h3>Comentarios</h3>
              %s
            </div>
            <div class="download">
              <a href="petsapp://post/%d"\s
                 onclick="setTimeout(() => { window.location.href='https://play.google.com/store/apps/details?id=tu.paquete' }, 2000)"\s
                 class="btn">
                 Ver en la App
              </a>
            </div>
          </div>
        </body>
        </html>
        """.formatted(
                    post.getTitle(),
                    post.getTitle(),
                    post.getContent(),
                    imageUrl,
                    post.getAuthor().getName(),
                    post.getCreatedAt().toLocalDate().toString(),
                    imageUrl,
                    post.getTitle(),
                    post.getContent(),
                    likeCount,
                    loveCount,
                    sadCount,
                    hopeCount,
                    commentsHtml,
                    post.getId()
            );

            return ResponseEntity.ok(html);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("<h1>Post no encontrado</h1>");
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
        public String country;
        public String city;
        public List<String> images;
    }
    public static class NewsCommentRequest{
        public Long id;
        public UUID authorId;
        public String content;
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
