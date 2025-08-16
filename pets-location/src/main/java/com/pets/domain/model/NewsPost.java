package com.pets.domain.model;

import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private User author;
    private String country;
    private String city;
    private List<String> images;
    private List<Comment> comments;
    private List<Reaction> reactions;
}
