package com.pets.news;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.pets.application.news.ReactToPostUseCase;
import com.pets.domain.model.NewsPost;
import com.pets.domain.model.Reaction;
import com.pets.domain.model.ReactionType;
import com.pets.domain.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
public class ReactToPostUseCaseTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private ReactToPostUseCase reactToPostUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void noDebePermitirReaccionDuplicadaPorUsuario() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Long postId = 1L;

        NewsPost existingPost = new NewsPost();
        existingPost.setId(postId);
        existingPost.setReactions(new ArrayList<>(
                List.of(new Reaction(null, userId, ReactionType.LIKE))
        ));

        when(newsRepository.findById(postId))
                .thenReturn(Optional.of(existingPost));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reactToPostUseCase.execute(postId, userId, ReactionType.LIKE)
        );

        assertEquals("El usuario ya reaccionó a este post", exception.getMessage());
        verify(newsRepository, never()).save(any());
    }

    @Test
    void debePermitirReaccionNuevaPorUsuarioDistinto() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Long postId = 1L;

        NewsPost existingPost = new NewsPost();
        existingPost.setId(postId);
        existingPost.setReactions(new ArrayList<>(
                List.of(new Reaction(null, userId1, ReactionType.LIKE))
        ));

        when(newsRepository.findById(postId))
                .thenReturn(Optional.of(existingPost));
        when(newsRepository.save(any(NewsPost.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        NewsPost updated = reactToPostUseCase.execute(postId, userId2, ReactionType.LIKE);

        // Assert
        assertEquals(2, updated.getReactions().size());
        verify(newsRepository).save(updated);
    }
}
