package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Integer requesterId);

    Page<ItemRequest> findAllByRequesterIdNot(Integer requesterId, Pageable pageable);
}