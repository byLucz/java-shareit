package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserValidDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(null, "John Doe", "john.doe@example.com");
        userRepository.save(user);
    }

    @Test
    void testCreateUser() {
        UserValidDto userValidDto = new UserValidDto(null, "Jane Doe", "jane.doe@example.com");

        UserValidDto createdUser = userService.createUser(userValidDto);

        assertNotNull(createdUser);
        assertEquals(userValidDto.getName(), createdUser.getName());
        assertEquals(userValidDto.getEmail(), createdUser.getEmail());

        assertTrue(userRepository.findById(createdUser.getId()).isPresent());
    }

    @Test
    void testGetUserById() {
        UserValidDto foundUser = userService.getUserById(user.getId());

        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto(user.getId(),"John Updated", "john.updated@example.com");

        UserValidDto updatedUser = userService.updateUser(user.getId(), userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getName(), updatedUser.getName());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());

        User updatedUserFromDb = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(updatedUserFromDb);
        assertEquals(userDto.getName(), updatedUserFromDb.getName());
        assertEquals(userDto.getEmail(), updatedUserFromDb.getEmail());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(user.getId());

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void testGetAllUsers() {
        List<UserValidDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getName(), users.get(0).getName());
    }
}