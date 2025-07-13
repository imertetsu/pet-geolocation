package com.pets.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsPost {
    private Long id;
    private String title;
    private String content;
    private NewsCategory category;
    private LocalDateTime createdAt;
    private UUID authorId;
    private String authorName;
    private List<String> images;
    private List<Comment> comments;
    private List<Reaction> reactions;
}
