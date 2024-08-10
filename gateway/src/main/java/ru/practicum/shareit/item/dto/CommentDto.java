package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Integer id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    @NotBlank(message = "Имя автора не может быть пустым")
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}