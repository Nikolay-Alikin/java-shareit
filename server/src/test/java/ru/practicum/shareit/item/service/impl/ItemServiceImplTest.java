package ru.practicum.shareit.item.service.impl;

import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.entity.enumerated.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.entity.RequestEntity;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.generated.model.dto.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemServiceImplTest")
class ItemServiceImplTest {

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private ItemJpaRepository itemRepository;
    @Mock
    private RequestJpaRepository requestJpaRepository;
    @Mock
    private BookingJpaRepository bookingJpaRepository;
    @Mock
    private CommentJpaRepository commentJpaRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Long xSHarerUserId = 1L;
    private final ItemDTO itemDTO = new ItemDTO()
            .name("Тестовый предмет")
            .description("Тестовое описание")
            .available(true);
    private final UserEntity userEntity = new UserEntity()
            .setId(1L)
            .setName("Тестовый пользователь")
            .setEmail("email@ru.ru");
    private final ItemEntity itemEntity = new ItemEntity()
            .setId(1L)
            .setOwner(userEntity)
            .setName(itemDTO.getName())
            .setDescription(itemDTO.getDescription())
            .setAvailable(true);
    private final UserEntity requestor = new UserEntity()
            .setId(2L)
            .setName("Тестовый пользователь")
            .setEmail("email@ru.ru");
    private final RequestEntity requestEntity = new RequestEntity()
            .setId(5L)
            .setDescription("Тестовое описание")
            .setRequestor(requestor)
            .setCreated(LocalDateTime.now());
    private final BookingEntity bookingEntity = new BookingEntity()
            .setId(1L)
            .setStart(LocalDateTime.now())
            .setEnd(LocalDateTime.now().plusDays(1))
            .setItemEntity(itemEntity)
            .setUserEntity(requestor)
            .setStatus(Status.APPROVED);
    private final CommentRequestDTO commentRequestDTO = new CommentRequestDTO()
            .text("Тестовый комментарий");
    private final CommentEntity commentEntity = new CommentEntity()
            .setId(1L)
            .setText(commentRequestDTO.getText())
            .setUserEntity(requestor)
            .setItemEntity(itemEntity);

    @Test
    @DisplayName("Test create item")
    void createItem() {
        Mockito.when(userJpaRepository.findById(xSHarerUserId))
                .thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(itemRepository.save(Mockito.any(ItemEntity.class))).thenReturn(itemEntity);

        ItemDTO result = itemService.createItem(xSHarerUserId, itemDTO);

        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(itemDTO);
    }

    @Test
    @DisplayName("Test createItem by request")
    void createItemByRequest() {
        itemDTO.setRequestId(5L);

        Mockito.when(userJpaRepository.findById(xSHarerUserId))
                .thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(requestJpaRepository.findById(5L))
                .thenReturn(Optional.ofNullable(requestEntity));
        Mockito.when(itemRepository.save(Mockito.any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                    ItemEntity itemEntity = invocation.getArgument(0);
                    itemEntity.setId(1L);
                    itemEntity.setRequestEntity(requestEntity);
                    return itemEntity;
                });

        ItemDTO result = itemService.createItem(xSHarerUserId, itemDTO);

        Assertions.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Тестовый предмет")
                .hasFieldOrPropertyWithValue("description", "Тестовое описание")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("requestId", 5L);
    }


    @Test
    @DisplayName("Test get empty owner items")
    void getEmptyUserItems() {
        Mockito.when(itemRepository.findAllByOwnerId(xSHarerUserId))
                .thenReturn(Collections.emptyList());

        List<ItemDTO> result = itemService.getUserItems(xSHarerUserId);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test get owner items")
    void getUserItems() {
        Mockito.when(itemRepository.findAllByOwnerId(xSHarerUserId))
                .thenReturn(List.of(itemEntity));

        List<ItemDTO> result = itemService.getUserItems(xSHarerUserId);

        Assertions.assertThat(result.getFirst()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(itemDTO);
    }

    @Test
    @DisplayName("Test get owner items with comment")
    void getUserItemsWithComment() {
        ItemDTO expectedResult = new ItemDTO()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .comments(List.of(new CommentResponseDTO()
                        .id(commentEntity.getId())
                        .text(commentEntity.getText())
                        .authorName(commentEntity.getUserEntity().getName())
                        .created(commentEntity.getCreated())
                ));
        Mockito.when(itemRepository.findAllByOwnerId(xSHarerUserId))
                .thenReturn(List.of(itemEntity));
        Mockito.when(commentJpaRepository.findAllByItemEntityIdIn(List.of(itemEntity.getId())))
                .thenReturn(List.of(commentEntity));

        List<ItemDTO> result = itemService.getUserItems(xSHarerUserId);

        Assertions.assertThat(result.getFirst()).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test get owner items with bookings")
    void getUserItemsWithBookings() {
        ItemDTO expectedResult = new ItemDTO()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .lastBooking(new LastBookingDTO()
                        .id(bookingEntity.getId())
                        .bookerId(bookingEntity.getUserEntity().getId()))
                .nextBooking(new NextBookingDTO()
                        .id(bookingEntity.getId())
                        .bookerId(bookingEntity.getUserEntity().getId()));

        Mockito.when(itemRepository.findAllByOwnerId(xSHarerUserId))
                .thenReturn(List.of(itemEntity));
        Mockito.when(bookingJpaRepository.findLastBookings(List.of(itemEntity.getId())))
                .thenReturn(Optional.of(List.of(bookingEntity)));
        Mockito.when(bookingJpaRepository.findNextBookings(List.of(itemEntity.getId())))
                .thenReturn(Optional.of(List.of(bookingEntity)));

        List<ItemDTO> result = itemService.getUserItems(xSHarerUserId);

        Assertions.assertThat(result.getFirst()).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test search items when search is empty")
    void searchItemsWhenSearchIsEmpty() {
        List<ItemDTO> itemDTOS = itemService.searchItems(xSHarerUserId, "");
        Assertions.assertThat(itemDTOS).isEmpty();

        Mockito.verifyNoInteractions(itemRepository);
    }

    @Test
    @DisplayName("Test search items")
    void searchItems() {
        Mockito.when(itemRepository.findItemEntitiesByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(List.of(itemEntity));
        List<ItemDTO> itemDTOS = itemService.searchItems(xSHarerUserId, "тест");

        Assertions.assertThat(itemDTOS).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Test get item when user not found")
    void getItemWhenUserNotFound() {
        Mockito.when(userJpaRepository.existsById(xSHarerUserId)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> itemService.getItem(xSHarerUserId, 1L))
                .isInstanceOf(NotFoundException.class);


    }

    @Test
    @DisplayName("Test get item when user is owner")
    void getItemWhenUserIsOwner() {
        Mockito.when(userJpaRepository.existsById(xSHarerUserId))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemEntity));
        Mockito.when(commentJpaRepository.findByItemEntityId(1L))
                .thenReturn(List.of(commentEntity));
        Mockito.when(bookingJpaRepository.findLastBooking(1L))
                .thenReturn(Optional.of(bookingEntity));
        Mockito.when(bookingJpaRepository.findNextBooking(1L))
                .thenReturn(Optional.of(bookingEntity));

        ItemDTO item = itemService.getItem(xSHarerUserId, 1L);

        Assertions.assertThat(item)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Тестовый предмет")
                .hasFieldOrPropertyWithValue("description", "Тестовое описание")
                .hasFieldOrPropertyWithValue("available", true);
        Assertions.assertThat(item.getLastBooking())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", 2L);
        Assertions.assertThat(item.getNextBooking())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", 2L);
        Assertions.assertThat(item.getComments())
                .size()
                .isEqualTo(1);

    }

    @Test
    @DisplayName("Test update item when user not owner")
    void updateItemWhenUserNotOwner() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemEntity));

        Assertions.assertThatThrownBy(() -> itemService.updateItem(10L, 1L, new ItemDTO()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Test update item")
    void updateItem() {
        itemDTO.setRequestId(5L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemEntity));
        Mockito.when(requestJpaRepository.findById(5L))
                .thenReturn(Optional.of(requestEntity));

        itemService.updateItem(1L, 1L, itemDTO);

        Mockito.verify(itemRepository)
                .save(Mockito.any(ItemEntity.class));
    }

    @Test
    @DisplayName("Test create comment when user can't create comment")
    void createCommentWhenUserCantCreateComment() {
        Mockito.when(bookingJpaRepository.existsByItemEntityIdAndUserEntityIdAndStatusIsAndEndBefore(Mockito.eq(1L),
                        Mockito.eq(xSHarerUserId), Mockito.eq(Status.APPROVED), Mockito.any(LocalDateTime.class)))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> itemService.createComment(xSHarerUserId, 1L, new CommentRequestDTO()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Test create comment")
    void createComment() {
        CommentResponseDTO expectedResult = new CommentResponseDTO()
                .authorName(userEntity.getName())
                .created(commentEntity.getCreated())
                .id(commentEntity.getId())
                .text(commentEntity.getText());

        Mockito.when(
                        bookingJpaRepository.existsByItemEntityIdAndUserEntityIdAndStatusIsAndEndBefore(Mockito.eq(1L),
                                Mockito.eq(xSHarerUserId), Mockito.eq(Status.APPROVED),
                                Mockito.any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(itemEntity));
        Mockito.when(userJpaRepository.findById(xSHarerUserId))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(commentJpaRepository.save(Mockito.any(CommentEntity.class)))
                .thenReturn(commentEntity);

        CommentResponseDTO result = itemService.createComment(xSHarerUserId, 1L, commentRequestDTO);

        Assertions.assertThat(result)
                .isEqualTo(expectedResult);
    }
}