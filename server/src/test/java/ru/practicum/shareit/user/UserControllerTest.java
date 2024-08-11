package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userValidDto;

    @BeforeEach
    void setUp() {
        userValidDto = new UserDto();
        userValidDto.setId(1);
        userValidDto.setName("Test User");
        userValidDto.setEmail("test@example.com");
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userValidDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userValidDto.getId()))
                .andExpect(jsonPath("$.name").value(userValidDto.getName()))
                .andExpect(jsonPath("$.email").value(userValidDto.getEmail()));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(userValidDto);

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userValidDto.getId()))
                .andExpect(jsonPath("$.name").value(userValidDto.getName()))
                .andExpect(jsonPath("$.email").value(userValidDto.getEmail()));

        verify(userService, times(1)).getUserById(anyInt());
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");
        userDto.setEmail("updated@example.com");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1);
        updatedUserDto.setName("Updated Name");
        updatedUserDto.setEmail("updated@example.com");

        when(userService.updateUser(anyInt(), any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedUserDto.getEmail()));

        verify(userService, times(1)).updateUser(anyInt(), any(UserDto.class));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyInt());
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userValidDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userValidDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userValidDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userValidDto.getEmail()));

        verify(userService, times(1)).getAllUsers();
    }
}
