package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserServiceException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserValidDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserValidDto createUser(UserValidDto userValidDto) {
        User user = UserMapper.toUser(userValidDto);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserValidDto getUser(Integer userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserValidDto updateUser(Integer userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            boolean emailExists = userRepository.findAll().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));
            if (emailExists) {
                throw new UserServiceException("Такая почта уже существует");
            }
        }
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserValidDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
