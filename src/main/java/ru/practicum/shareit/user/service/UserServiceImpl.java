package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserServiceException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserValidDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserValidDto createUser(UserValidDto userValidDto) {
        User user = UserMapper.toUser(userValidDto);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserValidDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserValidDto updateUser(Integer userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            boolean emailExists = userRepository.findByEmail(userDto.getEmail()).isPresent();
            if (emailExists) {
                throw new UserServiceException("Такая почта уже существует");
            }
        }
        user.setName(Optional.ofNullable(userDto.getName()).orElse(user.getName()));
        user.setEmail(Optional.ofNullable(userDto.getEmail()).orElse(user.getEmail()));
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserValidDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
