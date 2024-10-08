package ru.practicum.shareit.item.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWBooking {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @Nullable
    private BookingDto lastBooking;
    @Nullable
    private BookingDto nextBooking;
}
