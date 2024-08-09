package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDescDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Integer userId, ItemRequestDescDto dto);

    List<ItemResponseDto> getUserRequests(Integer userId);

    List<ItemResponseDto> getOtherUsersRequests(Integer userId, int from, int size);

    ItemResponseDto getRequestById(Integer requestId);
}
