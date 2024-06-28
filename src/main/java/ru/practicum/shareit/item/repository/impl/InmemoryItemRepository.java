package ru.practicum.shareit.item.repository.impl;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InmemoryItemRepository implements ItemRepository {

    private final Map<Long, ItemEntity> items = new HashMap<>();
    private final Map<Long, List<ItemEntity>> userItems = new HashMap<>();
    private long count = 0;

    @Override
    public ItemEntity createItem(ItemEntity itemEntity) {
        itemEntity.setId(++count);
        if (userItems.containsKey(itemEntity.getOwner().getId())) {
            userItems.put(itemEntity.getOwner().getId(), userItems.get(itemEntity.getOwner().getId()));
        }
        userItems.computeIfAbsent(itemEntity.getOwner().getId(), k -> new ArrayList<>()).add(itemEntity);
        items.put(itemEntity.getId(), itemEntity);
        return items.get(itemEntity.getId());
    }

    @Override
    public List<ItemEntity> getAllItems(Long xSharerUserId) {
        return userItems.get(xSharerUserId);
    }

    @Override
    public List<ItemEntity> searchItems(Long xSharerUserId, String search) {
        if (search.isBlank()) {
            return Collections.emptyList();
        }
        return userItems.values().stream()
                .flatMap(List::stream)
                .filter(item -> Boolean.TRUE == item.getAvailable()
                        && (item.getName().toLowerCase().contains(search.toLowerCase())
                        || item.getDescription().toLowerCase().contains(search.toLowerCase())))
                .toList();
    }

    @Override
    public ItemEntity getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public ItemEntity updateItem(ItemEntity updatedItem) {
        ItemEntity itemForUpdate = items.get(updatedItem.getId());
        if (!itemForUpdate.getOwner().equals(updatedItem.getOwner())) {
            throw new NotFoundException(
                    "У пользователя с id " + updatedItem.getOwner().getId() + " нет доступа к данному элементу");
        }
        itemForUpdate.setDescription(
                updatedItem.getDescription() == null ? itemForUpdate.getDescription() : updatedItem.getDescription());
        itemForUpdate.setName(updatedItem.getName() == null ? itemForUpdate.getName() : updatedItem.getName());
        itemForUpdate.setAvailable(
                updatedItem.getAvailable() == null ? itemForUpdate.getAvailable() : updatedItem.getAvailable());
        return items.get(updatedItem.getId());
    }
}