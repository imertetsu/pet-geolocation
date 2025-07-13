package com.pets.infrastructure.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NewsCommentDto {
    public Long id;
    public UUID authorId;
    public String authorName;
    public String content;
    public LocalDateTime createdAt;
}
