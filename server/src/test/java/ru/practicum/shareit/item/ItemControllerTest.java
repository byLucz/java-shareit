package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWBooking;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private ItemDtoWBookingAndComments itemDtoWBookingAndComments;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        itemDtoWBookingAndComments = new ItemDtoWBookingAndComments();
        itemDtoWBookingAndComments.setId(1);
        itemDtoWBookingAndComments.setName("Test Item With Booking");
        itemDtoWBookingAndComments.setDescription("Test Description With Booking");

        commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Test Comment");
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).createItem(any(ItemDto.class), anyInt());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Item\",\"description\":\"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).updateItem(any(ItemDto.class), anyInt(), anyInt());
    }

    @Test
    void getItemByIdAndUserId() throws Exception {
        when(itemService.getItemByIdAndUserId(anyInt(), anyInt())).thenReturn(itemDtoWBookingAndComments);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoWBookingAndComments.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoWBookingAndComments.getName()));

        verify(itemService, times(1)).getItemByIdAndUserId(anyInt(), anyInt());
    }

    @Test
    void getAllUserItems() throws Exception {
        ItemDtoWBooking itemDtoWBooking = new ItemDtoWBooking();
        itemDtoWBooking.setId(1);
        itemDtoWBooking.setName("Test Item With Booking");

        when(itemService.getAllUserItems(anyInt())).thenReturn(List.of(itemDtoWBooking));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoWBooking.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoWBooking.getName()));

        verify(itemService, times(1)).getAllUserItems(anyInt());
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItem(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService, times(1)).searchItem(anyString());
    }

    @Test
    void postComment() throws Exception {
        when(itemService.postComment(any(CommentDto.class), anyInt(), anyInt())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Test Comment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));

        verify(itemService, times(1)).postComment(any(CommentDto.class), anyInt(), anyInt());
    }
}