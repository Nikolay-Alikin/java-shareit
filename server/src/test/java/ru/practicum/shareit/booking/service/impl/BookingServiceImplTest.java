package ru.practicum.shareit.booking.service.impl;

import ru.practicum.shareit.booking.controller.enumerated.SearchState;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.entity.enumerated.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.generated.model.dto.BookingDTO;
import ru.yandex.practicum.generated.model.dto.BookingRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingServiceImplTest")
class BookingServiceImplTest {

    @Mock
    private ItemJpaRepository itemJpaRepository;
    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private BookingJpaRepository bookingJpaRepository;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequestDTO bookingRequestDTO;
    private UserEntity userEntity;
    private ItemEntity itemEntity;
    private BookingEntity bookingEntity;

    @BeforeEach
    void setUp() {
        bookingRequestDTO = new BookingRequestDTO()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(5));
        userEntity = new UserEntity()
                .setId(1L)
                .setEmail("email@ru.ru")
                .setName("name");
        itemEntity = new ItemEntity()
                .setId(1L)
                .setName("item name")
                .setDescription("item description")
                .setAvailable(true)
                .setOwner(userEntity);
        bookingEntity = new BookingEntity()
                .setItemEntity(itemEntity)
                .setUserEntity(userEntity)
                .setStart(LocalDateTime.now())
                .setEnd(LocalDateTime.now().plusDays(5))
                .setStatus(Status.WAITING);
    }

    @Test
    @DisplayName("Test create booking -> item not found")
    void createBookingItemNotFound() {
        Mockito.when(itemJpaRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.createBooking(1L, bookingRequestDTO))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("Test create booking -> booking exists")
    void createBookingWhenItExists() {
        Mockito.when(itemJpaRepository.findById(1L))
                .thenReturn(Optional.of(itemEntity));

        Mockito.when(bookingJpaRepository.existsByItemEntityAndTimeConflict(itemEntity, bookingRequestDTO.getStart(),
                        bookingRequestDTO.getEnd()))
                .thenReturn(true);

        Assertions.assertThatThrownBy(() -> bookingService.createBooking(1L, bookingRequestDTO)).isInstanceOf(
                BadRequestException.class);
    }

    @Test
    @DisplayName("Test create booking -> owner ad booker is same person")
    void createBookingWhenOwnerAndBookerAreTheSame() {
        Mockito.when(itemJpaRepository.findById(1L))
                .thenReturn(Optional.of(itemEntity));

        Mockito.when(bookingJpaRepository.existsByItemEntityAndTimeConflict(itemEntity, bookingRequestDTO.getStart(),
                        bookingRequestDTO.getEnd()))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> bookingService.createBooking(1L, bookingRequestDTO)).isInstanceOf(
                NotFoundException.class);
    }

    @Test
    @DisplayName("Test create booking")
    void createBooking() {
        Mockito.when(itemJpaRepository.findById(1L))
                .thenReturn(Optional.of(itemEntity));
        Mockito.when(bookingJpaRepository.existsByItemEntityAndTimeConflict(itemEntity, bookingRequestDTO.getStart(),
                        bookingRequestDTO.getEnd()))
                .thenReturn(false);
        Mockito.when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.save(Mockito.any(BookingEntity.class))).thenAnswer(invocation -> {
            BookingEntity savedEntity = invocation.getArgument(0, BookingEntity.class);
            savedEntity.setId(1L);
            return savedEntity;
        });

        BookingDTO result = bookingService.createBooking(2L, bookingRequestDTO);

        Assertions.assertThat(result)
                .usingRecursiveAssertion()
                .ignoringFields("id");
    }

    @Test
    @DisplayName("Test approve booking when it not found")
    void approveBookingWhenItsNotFound() {
        Mockito.when(bookingJpaRepository.findById(1L))
                .thenReturn(Optional.of(bookingEntity));

        Assertions.assertThatThrownBy(() -> bookingService.approveBooking(5L, 1L, true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Test approve booking when  it's approved")
    void approveBookingWhenItsApproved() {
        Mockito.when(bookingJpaRepository.findById(1L))
                .thenReturn(Optional.of(bookingEntity));

        bookingService.approveBooking(1L, 1L, true);

        Assertions.assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Test get booking not found")
    void getBookingNotFound() {
        bookingEntity.getItemEntity().getOwner().setId(5L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));

        Assertions.assertThatThrownBy(() -> bookingService.getBooking(1L, 1L))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("Test get booking")
    void getBooking() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));

        BookingDTO booking = bookingService.getBooking(1L, 1L);

        Assertions.assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(booking.getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(booking.getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings CURRENT")
    void getBookingsCURRENT() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findBookingsByUserEntityIdAndStartBeforeAndEndAfter(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getBookings(1L, SearchState.CURRENT);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings PAST")
    void getBookingsPAST() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findBookingsByUserEntityIdAndEndBefore(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getBookings(1L, SearchState.PAST);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings FUTURE")
    void getBookingsFUTURE() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findBookingsByUserEntityIdAndEndAfter(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getBookings(1L, SearchState.FUTURE);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings with WAITING status")
    void getBookingsWithWaitingStatus() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findAllByUserEntityIdAndStatusIs(1L, Status.WAITING
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getBookings(1L, SearchState.WAITING);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings with REJECTED status")
    void getBookingsWithRejectedStatus() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findAllByUserEntityIdAndStatusIs(1L, Status.REJECTED
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getBookings(1L, SearchState.REJECTED);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get owner bookings CURRENT")
    void getOwnerBookings() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findAllByItemEntityOwnerIdAndStartBeforeAndEndAfter(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getOwnerBookings(1L, SearchState.CURRENT);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings PAST")
    void getOwnerBookingsPAST() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findAllByItemEntityOwnerIdAndEndBefore(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getOwnerBookings(1L, SearchState.PAST);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings FUTURE")
    void getOwnerBookingsFUTURE() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Mockito.when(bookingJpaRepository.findAllByItemEntityOwnerIdAndEndAfter(
                Mockito.eq(Long.valueOf(1L)),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getOwnerBookings(1L, SearchState.FUTURE);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings with WAITING status")
    void getOwnerBookingsWithWaitingStatus() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findAllByItemEntityOwnerIdAndStatusIs(1L, Status.WAITING
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getOwnerBookings(1L, SearchState.WAITING);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DisplayName("Test get bookings with REJECTED status")
    void getOwnerBookingsWithRejectedStatus() {
        bookingEntity.setId(1L);
        Mockito.when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(bookingJpaRepository.findAllByItemEntityOwnerIdAndStatusIs(1L, Status.REJECTED
        )).thenReturn(List.of(bookingEntity));

        List<BookingDTO> bookings = bookingService.getOwnerBookings(1L, SearchState.REJECTED);

        Assertions.assertThat(bookings).hasSize(1);
        Assertions.assertThat(bookings.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", bookingEntity.getStart())
                .hasFieldOrPropertyWithValue("end", bookingEntity.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingEntity.getStatus().name());
        Assertions.assertThat(bookings.getFirst().getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item name");
        Assertions.assertThat(bookings.getFirst().getBooker())
                .hasFieldOrPropertyWithValue("id", 1L);
    }
}