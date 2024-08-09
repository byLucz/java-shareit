package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.request.dto.ItemRequestDescDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.repo.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(Integer userId, ItemRequestDescDto dto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);
        itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getUserRequests(Integer userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findAllByRequestId(request.getId());
                    return ItemRequestMapper.toItemResponseDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getOtherUsersRequests(Integer userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = (List<ItemRequest>) itemRequestRepository.findAllByRequesterIdNot(userId, pageable);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findAllByRequestId(request.getId());
                    return ItemRequestMapper.toItemResponseDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getRequestById(Integer requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemResponseDto(request, items);
    }
}
