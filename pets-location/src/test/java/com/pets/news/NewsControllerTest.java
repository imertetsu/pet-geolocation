package com.pets.news;

import com.pets.application.news.CreateNewsPostUseCase;
import com.pets.application.news.DeleteNewsPostUseCase;
import com.pets.application.news.GetNewsPostByIdUseCase;
import com.pets.application.news.ReactToPostUseCase;
import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsControllerTest {

    private NewsRepository newsRepository;
    private CreateNewsPostUseCase createNewsPostUseCase;
    private GetNewsPostByIdUseCase getNewsPostByIdUseCase;
    private ReactToPostUseCase reactToPostUseCase;
    private DeleteNewsPostUseCase deleteNewsPostUseCase;

    @BeforeEach
    void setUp() {
        newsRepository = mock(NewsRepository.class);
        createNewsPostUseCase = new CreateNewsPostUseCase(newsRepository);
        getNewsPostByIdUseCase = new GetNewsPostByIdUseCase(newsRepository);
        reactToPostUseCase = new ReactToPostUseCase(newsRepository);
        deleteNewsPostUseCase = new DeleteNewsPostUseCase(newsRepository);
    }

    @Test
    void shouldCreateNewsPostSuccessfully() {
        // Arrange
        UUID authorId = UUID.randomUUID();
        List<String> images = Arrays.asList("img1.jpg", "img2.jpg");
        NewsPost savedPost = new NewsPost(1L, "Nuevo post", "Contenido", NewsCategory.LOST_PET,
                LocalDateTime.now(), authorId, "Autor", "Pais", "Ciudad", images,
                new ArrayList<>(), new ArrayList<>());
        when(newsRepository.save(any())).thenReturn(savedPost);

        // Act
        NewsPost result = createNewsPostUseCase.execute(
                "Nuevo post",
                "Contenido",
                NewsCategory.LOST_PET,
                authorId,
                "Autor",
                "Pais",
                "Ciudad",
                images
        );
        // Assert
        assertNotNull(result);
        assertEquals("Nuevo post", result.getTitle());
        verify(newsRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnNewsPostById() {
        // Arrange
        NewsPost post = new NewsPost(1L, "Post existente", "Contenido", NewsCategory.LOST_PET,
                LocalDateTime.now(), UUID.randomUUID(), "Autor", "Pais", "Ciudad",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(newsRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        NewsPost result = getNewsPostByIdUseCase.execute(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Post existente", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        // Arrange
        when(newsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> getNewsPostByIdUseCase.execute(99L));
        assertEquals("News post not found with ID: 99", ex.getMessage());
    }

    @Test
    void shouldAddReaction() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NewsPost post = new NewsPost(1L, "Post", "Contenido", NewsCategory.LOST_PET,
                LocalDateTime.now(), UUID.randomUUID(), "Autor", "Pais", "Ciudad",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(newsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(newsRepository.save(any())).thenReturn(post);

        // Act
        NewsPost result = reactToPostUseCase.execute(1L, userId, ReactionType.LIKE);

        // Assert
        assertTrue(result.getReactions().stream().anyMatch(r -> r.getUserId().equals(userId)));
        verify(newsRepository, times(1)).save(post);
    }

    @Test
    void shouldDeletePost() {
        // Arrange
        when(newsRepository.findById(1L)).thenReturn(Optional.of(mock(NewsPost.class)));
        doNothing().when(newsRepository).deleteById(1L);

        // Act
        deleteNewsPostUseCase.execute(1L);

        // Assert
        verify(newsRepository, times(1)).deleteById(1L);
    }
}
