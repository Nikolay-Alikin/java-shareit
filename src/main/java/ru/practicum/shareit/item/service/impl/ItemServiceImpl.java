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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generated.model.dto.CommentRequestDTO;
import ru.yandex.practicum.generated.model.dto.CommentResponseDTO;
import ru.yandex.practicum.generated.model.dto.ItemDTO;
import ru.yandex.practicum.generated.model.dto.LastBookingDTO;
import ru.yandex.practicum.generated.model.dto.NextBookingDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Предмет не найден. id=";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден. id=";

    private final ItemMapper itemMapper;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDTO createItem(Long xSharerUserId, ItemDTO itemDTO) {
        UserEntity userEntity = getUserEntity(xSharerUserId);
        ItemEntity entity = itemMapper.toEntity(itemDTO);
        entity.setOwner(userEntity);
        return itemMapper.toDto(itemRepository.save(entity));
    }

    @Override
    public List<ItemDTO> getUserItems(Long xSharerUserId) {
        List<ItemEntity> ownerItems = itemRepository.findAllByOwnerId(xSharerUserId);
        Map<Long, ItemDTO> itemEntityMap = ownerItems.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toMap(ItemDTO::getId, dto -> dto));

        if (ownerItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemIds = itemEntityMap.keySet().stream().toList();

        Optional<List<BookingEntity>> optionalLastBookings = bookingJpaRepository.findLastBookings(itemIds);
        Optional<List<BookingEntity>> optionalNextBookings = bookingJpaRepository.findNextBookings(itemIds);
        List<CommentEntity> comments = commentJpaRepository.findAllByItemEntityIdIn(itemIds);

        optionalLastBookings.ifPresent(lastBookings -> lastBookings.forEach(lastBooking -> {
            ItemDTO itemDTO = itemEntityMap.get(lastBooking.getItemEntity().getId());
            itemDTO.lastBooking(new LastBookingDTO()
                    .id(lastBooking.getId())
                    .bookerId(lastBooking.getUserEntity().getId()));

        }));
        optionalNextBookings.ifPresent(nextBookings -> nextBookings.forEach(nextBooking -> {
            ItemDTO itemDTO = itemEntityMap.get(nextBooking.getItemEntity().getId());
            itemDTO.setNextBooking(new NextBookingDTO()
                    .id(nextBooking.getId())
                    .bookerId(nextBooking.getUserEntity().getId()));
        }));
        comments.forEach(comment -> itemEntityMap.get(comment.getItemEntity().getId()).getComments()
                .add(commentMapper.toResponse(comment)));

        return new ArrayList<>(itemEntityMap.values());
    }

    @Override
    public List<ItemDTO> searchItems(Long xSharerUserId, String search) {
        if (search.isBlank() || search.isEmpty()) {
            return Collections.emptyList();
        }
        return itemMapper.toDto(
                itemRepository.findItemEntitiesByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(search,
                        search));
    }

    @Override
    public ItemDTO getItem(Long xSharerUserId, Long itemId) {
        if (!userRepository.existsById(xSharerUserId)) {
            logUserNotFound(xSharerUserId);
            throw getNotFoundException(USER_NOT_FOUND_MESSAGE + xSharerUserId);
        }
        ItemEntity entity = getItemEntity(itemId);
        ItemDTO dto = itemMapper.toDto(entity);
        dto.setComments(
                commentJpaRepository.findByItemEntityId(itemId).stream().map(commentMapper::toResponse).toList());

        if (entity.getOwner().getId().equals(xSharerUserId)) {
            bookingJpaRepository.findLastBooking(itemId).ifPresent(b -> {
                LastBookingDTO lastBookingDTO = new LastBookingDTO();
                lastBookingDTO.setId(b.getId());
                lastBookingDTO.setBookerId(b.getUserEntity().getId());
                dto.setLastBooking(lastBookingDTO);
            });
            bookingJpaRepository.findNextBooking(itemId).ifPresent(b -> {
                NextBookingDTO nextBookingDTO = new NextBookingDTO();
                nextBookingDTO.setId(b.getId());
                nextBookingDTO.setBookerId(b.getUserEntity().getId());
                dto.setNextBooking(nextBookingDTO);
            });
            return dto;
        }
        return dto;
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long xSharerUserId, Long itemId, ItemDTO itemDTO) {
        ItemEntity itemEntity = getItemEntity(itemId);
        if (!itemEntity.getOwner().getId().equals(xSharerUserId)) {
            logUserNotFound(xSharerUserId);
            throw getNotFoundException("У пользователя с id " + xSharerUserId + " нет доступа к данному элементу");
        }
        itemMapper.updateItem(itemEntity, itemDTO);
        return itemMapper.toDto(itemRepository.save(itemEntity));
    }

    @Override
    @Transactional
    public CommentResponseDTO createComment(Long xSharerUserId, Long itemId, CommentRequestDTO commentRequestDTO) {
        final LocalDateTime now = LocalDateTime.now();
        if (!bookingJpaRepository.existsByItemEntityIdAndUserEntityIdAndStatusIsAndEndBefore(itemId, xSharerUserId,
                Status.APPROVED,now)) {
            throw new BadRequestException("Пользователь с id " + xSharerUserId + " не может оставлять комментарии");
        }
        ItemEntity item = getItemEntity(itemId);
        UserEntity booker = getUserEntity(xSharerUserId);

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setItemEntity(item);
        commentEntity.setText(commentRequestDTO.getText());
        commentEntity.setUserEntity(booker);

        return commentMapper.toResponse(commentJpaRepository.save(commentEntity));
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            logUserNotFound(userId);
            return getNotFoundException(USER_NOT_FOUND_MESSAGE + userId);
        });
    }

    private ItemEntity getItemEntity(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> getNotFoundException(ITEM_NOT_FOUND_MESSAGE + itemId));
    }

    private NotFoundException getNotFoundException(String message) {
        return new NotFoundException(message);
    }

    private void logUserNotFound(long userId) {
        log.error(USER_NOT_FOUND_MESSAGE + "{}", userId);
    }
}