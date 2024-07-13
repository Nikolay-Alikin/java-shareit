package ru.practicum.shareit.item.service;

import java.util.List;
import ru.yandex.practicum.generated.model.dto.CommentRequestDTO;
import ru.yandex.practicum.generated.model.dto.CommentResponseDTO;
import ru.yandex.practicum.generated.model.dto.ItemDTO;

public interface ItemService {

    ItemDTO createItem(Long xSharerUserId, ItemDTO itemDTO);

    List<ItemDTO> getUserItems(Long xSharerUserId);

    List<ItemDTO> searchItems(Long xSharerUserId, String search);

    ItemDTO getItem(Long xSharerUserId, Long itemId);

    ItemDTO updateItem(Long xSharerUserId, Long itemId, ItemDTO itemDTO);

    CommentResponseDTO createComment(Long xSharerUserId, Long itemId, CommentRequestDTO commentRequestDTO);
}