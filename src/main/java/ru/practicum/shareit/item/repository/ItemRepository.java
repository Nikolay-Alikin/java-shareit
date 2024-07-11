package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.entity.ItemEntity;
import java.util.List;

public interface ItemRepository {

    ItemEntity createItem(ItemEntity itemEntity);

    List<ItemEntity> getAllItems(Long xSharerUserId);

    List<ItemEntity> searchItems(Long xSharerUserId, String search);

    ItemEntity getItem(Long itemId);

    ItemEntity updateItem(ItemEntity entity);
}