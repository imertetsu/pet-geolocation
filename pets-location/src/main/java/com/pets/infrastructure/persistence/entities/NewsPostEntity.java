package com.pets.infrastructure.persistence.entities;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "news_posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    private String country;
    private String city;

    @ElementCollection
    @CollectionTable(name = "news_post_images", joinColumns = @JoinColumn(name = "news_post_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @OneToMany(mappedBy = "newsPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "newsPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsReactionEntity> reactions = new ArrayList<>();

}

