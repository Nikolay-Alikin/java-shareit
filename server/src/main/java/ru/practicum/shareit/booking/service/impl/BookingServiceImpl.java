package ru.practicum.shareit.booking.service.impl;

import ru.practicum.shareit.booking.controller.enumerated.SearchState;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.entity.enumerated.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generated.model.dto.BookingDTO;
import ru.yandex.practicum.generated.model.dto.BookingRequestDTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemJpaRepository itemJpaRepository;
    private final BookingMapper bookingMapper;
    private final UserJpaRepository userJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Override
    @Transactional
    public BookingDTO createBooking(Long xSharerUserId, BookingRequestDTO bookingRequestDTO) {
        ItemEntity bookingItem = itemJpaRepository.findById(bookingRequestDTO.getItemId())
                .orElseThrow(
                        () -> new NotFoundException("Вещь с id=" + bookingRequestDTO.getItemId() + " не найдена"));

        if (!bookingItem.getAvailable() || bookingJpaRepository.existsByItemEntityAndTimeConflict(
                bookingItem,
                bookingRequestDTO.getStart(),
                bookingRequestDTO.getEnd())) {
            throw new BadRequestException(
                    "Вещь с id=" + bookingRequestDTO.getItemId() + " недоступна для бронирования");
        }
        if (bookingItem.getOwner().getId().equals(xSharerUserId)) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        UserEntity booker = findUserEntity(xSharerUserId);

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setItemEntity(bookingItem);
        bookingEntity.setUserEntity(booker);
        bookingEntity.setStart(bookingRequestDTO.getStart());
        bookingEntity.setEnd(bookingRequestDTO.getEnd());
        bookingEntity.setStatus(Status.WAITING);

        BookingEntity createdBooking = bookingJpaRepository.save(bookingEntity);

        return bookingMapper.toDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDTO approveBooking(Long xSharerUserId, Long bookingId, Boolean approved) {
        BookingEntity booking = findBookingEntity(bookingId);
        if (!booking.getItemEntity().getOwner().getId().equals(xSharerUserId)) {
            throw new NotFoundException("У вас нет доступа к этой вещи");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже подтверждено");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toDto(bookingJpaRepository.save(booking));
    }

    @Override
    public BookingDTO getBooking(Long xSharerUserId, Long bookingId) {
        findUserEntity(xSharerUserId);
        BookingEntity bookingEntity = findBookingEntity(bookingId);
        if (!bookingEntity.getUserEntity().getId().equals(xSharerUserId)
            && !bookingEntity.getItemEntity().getOwner().getId().equals(xSharerUserId)) {
            throw new NotFoundException("У вас нет доступа к этой брони");
        }
        return bookingMapper.toDto(bookingEntity);
    }

    @Override
    public List<BookingDTO> getBookings(Long xSharerUserId, SearchState searchState) {
        findUserEntity(xSharerUserId);
        LocalDateTime now = LocalDateTime.now();
        List<BookingDTO> result =
                switch (searchState) {
                    case CURRENT -> bookingMapper.toDto(bookingJpaRepository
                            .findBookingsByUserEntityIdAndStartBeforeAndEndAfter(xSharerUserId, now,
                                    now));
                    case PAST -> bookingMapper.toDto(bookingJpaRepository
                            .findBookingsByUserEntityIdAndEndBefore(xSharerUserId, now));
                    case FUTURE -> bookingMapper.toDto(bookingJpaRepository
                            .findBookingsByUserEntityIdAndEndAfter(xSharerUserId, now));
                    case WAITING -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByUserEntityIdAndStatusIs(xSharerUserId, Status.WAITING));
                    case REJECTED -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByUserEntityIdAndStatusIs(xSharerUserId, Status.REJECTED));
                    default -> bookingMapper.toDto(bookingJpaRepository.findAllByUserEntityId(xSharerUserId));
                };
        result.sort(Comparator.comparing(BookingDTO::getStart).reversed());
        return result;
    }

    @Override
    public List<BookingDTO> getOwnerBookings(Long xSharerUserId, SearchState searchState) {
        findUserEntity(xSharerUserId);
        LocalDateTime now = LocalDateTime.now();
        List<BookingDTO> result =
                switch (searchState) {
                    case CURRENT -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByItemEntityOwnerIdAndStartBeforeAndEndAfter(xSharerUserId, now, now));
                    case PAST -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByItemEntityOwnerIdAndEndBefore(xSharerUserId, now));
                    case FUTURE -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByItemEntityOwnerIdAndEndAfter(xSharerUserId, now));
                    case WAITING -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByItemEntityOwnerIdAndStatusIs(xSharerUserId, Status.WAITING));
                    case REJECTED -> bookingMapper.toDto(bookingJpaRepository
                            .findAllByItemEntityOwnerIdAndStatusIs(xSharerUserId, Status.REJECTED));
                    default -> bookingMapper.toDto(bookingJpaRepository.findAllByItemEntityOwnerId(xSharerUserId));
                };
        result.sort(Comparator.comparing(BookingDTO::getStart).reversed());
        return result;
    }

    private BookingEntity findBookingEntity(Long bookingId) {

        return bookingJpaRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
    }

    private UserEntity findUserEntity(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
