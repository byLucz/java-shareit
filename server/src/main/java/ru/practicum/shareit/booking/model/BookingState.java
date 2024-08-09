package ru.practicum.shareit.booking.model;

public enum BookingState {
    WAITING,
    APPROVED,
    REJECTED,
    ALL,
    CURRENT,
    PAST,
    FUTURE;
    public static BookingState findByName(String name) {
        for (BookingState state : values())
            if (state.name().equalsIgnoreCase(name))
                return state;
        return null;
    }
}
