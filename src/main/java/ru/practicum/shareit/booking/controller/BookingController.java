package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.controller.enumerated.SearchState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.BookingsApi;
import ru.yandex.practicum.generated.model.dto.BookingDTO;
import ru.yandex.practicum.generated.model.dto.BookingRequestDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController implements BookingsApi {

    private final BookingService bookingService;

    @Override
    public ResponseEntity<BookingDTO> createBooking(Long xSharerUserId, BookingRequestDTO bookingRequestDTO) {
        log.info("Поступил запрос на создание бронирования={} пользователем с id={}", bookingRequestDTO, xSharerUserId);
        validateBookingPeriod(bookingRequestDTO);
        BookingDTO bookingDTO = bookingService.createBooking(xSharerUserId, bookingRequestDTO);
        log.info("Бронирование создано: {}", bookingDTO);
        return ResponseEntity.status(201).body(bookingDTO);
    }

    @Override
    public ResponseEntity<BookingDTO> approveBooking(Long xSharerUserId, Long bookingId, Boolean approved) {
        log.info("Поступил запрос на подтверждение бронирования={} пользователем с id={}", bookingId, xSharerUserId);
        BookingDTO bookingDTO = bookingService.approveBooking(xSharerUserId, bookingId, approved);
        log.info("Бронирование подтверждено: {}", bookingDTO);
        return ResponseEntity.ok(bookingDTO);
    }


    @Override
    public ResponseEntity<BookingDTO> getBooking(Long xSharerUserId, Long bookingId) {
        log.info("Поступил запрос на получение бронирования={} пользователем с id={}", bookingId, xSharerUserId);
        BookingDTO bookingDTO = bookingService.getBooking(xSharerUserId, bookingId);
        log.info("Бронирование получено: {}", bookingDTO);
        return ResponseEntity.ok(bookingDTO);
    }

    @Override
    public ResponseEntity<List<BookingDTO>> getBookings(Long xSharerUserId, String state) {
        log.info("Поступил запрос на получение бронирований пользователем с id={} и state={}", xSharerUserId, state);
        List<BookingDTO> bookings = bookingService.getBookings(xSharerUserId, getSearchState(state));
        log.info("Бронирования получены: {}", bookings);
        return ResponseEntity.ok(bookings);
    }

    @Override
    public ResponseEntity<List<BookingDTO>> getOwnerBookings(Long xSharerUserId, String state) {
        log.info("Поступил запрос на получение бронирований владельцем={} и state={}", xSharerUserId, state);
        List<BookingDTO> bookings = bookingService.getOwnerBookings(xSharerUserId,
                getSearchState(state));
        log.info("Бронирования владельца получены: {}", bookings);
        return ResponseEntity.ok(bookings);
    }

    private void validateBookingPeriod(BookingRequestDTO request) {
        if (request.getEnd().isBefore(request.getStart())) {
            throw new BadRequestException("Окончание срока бронирования не может быть раньше начала");
        }
        if (request.getStart().equals(request.getEnd())) {
            throw new BadRequestException("Начало и окончание срока бронирования не могут совпадать");
        }
    }

    private SearchState getSearchState(String state) {
        try {
            return SearchState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}