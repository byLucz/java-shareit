package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWBooking;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(null, "Test User", "user@test.com");
        owner = new User(null, "Owner", "owner@test.com");
        userRepository.save(user);
        userRepository.save(owner);

        item = new Item(null, "Test Item", "Test Description", true, owner, null);
        itemRepository.save(item);
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = new ItemDto(null, "New Item", "New Description", true, null);

        ItemDto createdItem = itemService.createItem(itemDto, owner.getId());

        assertNotNull(createdItem);
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());

        assertTrue(itemRepository.findById(createdItem.getId()).isPresent());
    }

    @Test
    void testUpdateItem() {
        ItemDto itemDto = new ItemDto(null, "Updated Item", "Updated Description", false, null);

        ItemDto updatedItem = itemService.updateItem(itemDto, item.getId(), owner.getId());

        assertNotNull(updatedItem);
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());

        assertEquals("Updated Item", itemRepository.findById(item.getId()).get().getName());
    }

    @Test
    void testGetItemByIdAndUserId() {
        ItemDtoWBookingAndComments itemWithDetails = itemService.getItemByIdAndUserId(owner.getId(), item.getId());

        assertNotNull(itemWithDetails);
        assertEquals(item.getId(), itemWithDetails.getId());
        assertEquals(item.getName(), itemWithDetails.getName());
    }

    @Test
    void testGetAllUserItems() {
        List<ItemDtoWBooking> items = itemService.getAllUserItems(owner.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void testSearchItem() {
        List<ItemDto> items = itemService.searchItem("Test");

        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void testPostComment() {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
        bookingRepository.save(bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED)));

        CommentDto postedComment = itemService.postComment(commentDto, user.getId(), item.getId());

        assertNotNull(postedComment);
        assertEquals(commentDto.getText(), postedComment.getText());

        assertTrue(commentRepository.findById(postedComment.getId()).isPresent());
    }
}
