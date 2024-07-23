package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.enumerated.SearchState;
import java.util.List;
import ru.yandex.practicum.generated.model.dto.BookingDTO;
import ru.yandex.practicum.generated.model.dto.BookingRequestDTO;

public interface BookingService {

    BookingDTO createBooking(Long xSharerUserId, BookingRequestDTO bookingRequestDTO);

    BookingDTO approveBooking(Long xSharerUserId, Long bookingId, Boolean approved);

    BookingDTO getBooking(Long xSharerUserId, Long bookingId);

    List<BookingDTO> getBookings(Long xSharerUserId, SearchState searchState);

    List<BookingDTO> getOwnerBookings(Long xSharerUserId, SearchState searchState);
}
