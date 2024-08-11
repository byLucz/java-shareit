package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDescDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        user = new User(null, "Test User", "user@test.com");
        anotherUser = new User(null, "Another User", "another@test.com");
        userRepository.save(user);
        userRepository.save(anotherUser);
    }

    @Test
    void testCreateRequest() {
        ItemRequestDescDto requestDescDto = new ItemRequestDescDto("Я прикупил огромный байк...");

        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), requestDescDto);

        assertNotNull(createdRequest);
        assertNotNull(createdRequest.getRequester());
        assertEquals(requestDescDto.getDescription(), createdRequest.getDescription());
        assertEquals(user.getId(), createdRequest.getRequester().getId());

        assertTrue(itemRequestRepository.findById(createdRequest.getId()).isPresent());
    }

    @Test
    void testGetUserRequests() {
        ItemRequest request = new ItemRequest(null, "Я прикупил огромный байк...", LocalDateTime.now(), user);
        itemRequestRepository.save(request);

        List<ItemResponseDto> userRequests = itemRequestService.getUserRequests(user.getId());

        assertFalse(userRequests.isEmpty());
        assertEquals(1, userRequests.size());
        assertEquals(request.getDescription(), userRequests.get(0).getDescription());
    }

    @Test
    void testGetOtherUsersRequests() {
        ItemRequest request = new ItemRequest(null, "Я прикупил огромный макбук...", LocalDateTime.now(), anotherUser);
        itemRequestRepository.save(request);

        List<ItemResponseDto> otherUserRequests = itemRequestService.getOtherUsersRequests(user.getId(), 0, 10);

        assertFalse(otherUserRequests.isEmpty());
        assertEquals(1, otherUserRequests.size());
        assertEquals(request.getDescription(), otherUserRequests.get(0).getDescription());
    }

    @Test
    void testGetRequestById() {
        ItemRequest request = new ItemRequest(null, "Я прикупил огромный корч...", LocalDateTime.now(), anotherUser);
        itemRequestRepository.save(request);

        ItemResponseDto foundRequest = itemRequestService.getRequestById(request.getId());

        assertNotNull(foundRequest);
        assertEquals(request.getDescription(), foundRequest.getDescription());
    }
}
