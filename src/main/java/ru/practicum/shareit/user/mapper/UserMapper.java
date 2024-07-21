package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserValidDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserValidDto toUserDto(User user) {
        return new UserValidDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserValidDto userValidDto) {
        User user = new User();
        user.setName(userValidDto.getName());
        user.setEmail(userValidDto.getEmail());
        return user;
    }
}
