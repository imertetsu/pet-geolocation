package com.pets.infrastructure.controllers.dto;

import com.pets.domain.model.NewsCategory;
import com.pets.domain.model.ReactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class NewsPostDto {
    public Long id;
    public String title;
    public String content;
    public NewsCategory category;
    public LocalDateTime createdAt;
    public UserDto author;
    public List<String> images;
    public List<NewsCommentDto> comments;
    public Map<ReactionType, Long> reactions;
    public String userReaction;
}