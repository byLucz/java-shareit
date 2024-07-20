package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserValidDto;

import java.util.List;

public interface UserService {
    UserValidDto createUser(UserValidDto userValidDto);
    UserValidDto getUser(Integer userId);
    UserValidDto updateUser(Integer userId, UserDto userDto);
    void deleteUser(Integer userId);
    List<UserValidDto> getAllUsers();
}
