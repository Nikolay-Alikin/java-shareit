package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.feign.BookingClient;
import ru.practicum.shareit.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.BookingsApi;
import ru.yandex.practicum.generated.model.dto.BookingDTO;
import ru.yandex.practicum.generated.model.dto.BookingRequestDTO;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController implements BookingsApi {

    private final BookingClient feignClient;

    @Override
    public ResponseEntity<BookingDTO> createBooking(Long xSharerUserId, BookingRequestDTO bookingRequestDTO) {
        log.info("Поступил запрос на создание бронирования={} пользователем с id={}", bookingRequestDTO, xSharerUserId);
        validateBookingPeriod(bookingRequestDTO);
        ResponseEntity<BookingDTO> response = feignClient.createBooking(xSharerUserId, bookingRequestDTO);
        log.info("Бронирование создано: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<BookingDTO> approveBooking(Long xSharerUserId, Long bookingId, Boolean approved) {
        log.info("Поступил запрос на подтверждение бронирования={} пользователем с id={}", bookingId, xSharerUserId);
        ResponseEntity<BookingDTO> response = feignClient.approveBooking(xSharerUserId, bookingId, approved);
        log.info("Бронирование подтверждено: {}", response.getBody());
        return response;
    }


    @Override
    public ResponseEntity<BookingDTO> getBooking(Long xSharerUserId, Long bookingId) {
        log.info("Поступил запрос на получение бронирования={} пользователем с id={}", bookingId, xSharerUserId);
        ResponseEntity<BookingDTO> response = feignClient.getBooking(xSharerUserId, bookingId);
        log.info("Бронирование получено: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<BookingDTO>> getBookings(Long xSharerUserId, String state) {
        log.info("Поступил запрос на получение бронирований пользователем с id={} и state={}", xSharerUserId, state);
        ResponseEntity<List<BookingDTO>> response = feignClient.getBookings(xSharerUserId, state);
        log.info("Бронирования получены: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<BookingDTO>> getOwnerBookings(Long xSharerUserId, String state) {
        log.info("Поступил запрос на получение бронирований владельцем={} и state={}", xSharerUserId, state);
        ResponseEntity<List<BookingDTO>> response = feignClient.getOwnerBookings(xSharerUserId,
                state);
        log.info("Бронирования владельца получены: {}", response.getBody());
        return response;
    }

    private void validateBookingPeriod(BookingRequestDTO request) {
        if (request.getEnd().isBefore(request.getStart())) {
            throw new BadRequestException("Окончание срока бронирования не может быть раньше начала");
        }
        if (request.getStart().equals(request.getEnd())) {
            throw new BadRequestException("Начало и окончание срока бронирования не могут совпадать");
        }
    }
}
