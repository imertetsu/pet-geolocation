package com.pets.infrastructure.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewsCommentDto {
    public Long id;
    public AuthorUserDto author;
    public String content;
    public LocalDateTime createdAt;
}
