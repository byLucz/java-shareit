package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDescDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Test Request");

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1);
        itemResponseDto.setDescription("Test Response");
    }

    @Test
    void createRequest() throws Exception {
        when(itemRequestService.createRequest(anyInt(), any(ItemRequestDescDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test Request\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1)).createRequest(anyInt(), any(ItemRequestDescDto.class));
    }

    @Test
    void getUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()));

        verify(itemRequestService, times(1)).getUserRequests(anyInt());
    }

    @Test
    void getOtherUsersRequests() throws Exception {
        when(itemRequestService.getOtherUsersRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()));

        verify(itemRequestService, times(1)).getOtherUsersRequests(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyInt())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()));

        verify(itemRequestService, times(1)).getRequestById(anyInt());
    }
}
