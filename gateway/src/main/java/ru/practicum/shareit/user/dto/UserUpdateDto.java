package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
        private Integer id;

        private String name;

        private String email;
}
