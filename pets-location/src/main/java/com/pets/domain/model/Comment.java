package com.pets.domain.model;

import com.pets.infrastructure.controllers.dto.AuthorUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
    private AuthorUserDto author;
    private String content;
    private LocalDateTime createdAt;

}
