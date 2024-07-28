package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.feign.ItemClient;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.ItemsApi;
import ru.yandex.practicum.generated.model.dto.CommentRequestDTO;
import ru.yandex.practicum.generated.model.dto.CommentResponseDTO;
import ru.yandex.practicum.generated.model.dto.ItemDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController implements ItemsApi {

    private final ItemClient client;

    @Override
    public ResponseEntity<ItemDTO> createItem(Long xSharerUserId, ItemDTO itemDTO) {
        log.info("Запрос на создание item={} от пользователя с id={}", itemDTO, xSharerUserId);
        checkCreatedItem(itemDTO);
        ResponseEntity<ItemDTO> response = client.createItem(xSharerUserId, itemDTO);
        log.info("Создан item={} для пользователя с id={}", response.getBody(), xSharerUserId);
        return response;
    }

    @Override
    public ResponseEntity<List<ItemDTO>> getAllItems(Long xSharerUserId) {
        log.info("Запрос на получение всех items для пользователя с id={}", xSharerUserId);
        ResponseEntity<List<ItemDTO>> response = client.getAllItems(xSharerUserId);
        log.info("Получены items={} для пользователя с id={}", response.getBody(), xSharerUserId);
        return response;
    }

    @Override
    public ResponseEntity<ItemDTO> getItem(Long xSharerUserId, Long itemId) {
        log.info("Запрос на получение item={} для пользователя с id={}", itemId, xSharerUserId);
        ResponseEntity<ItemDTO> response = client.getItem(xSharerUserId, itemId);
        log.info("Получен item={} для пользователя с id={}", response.getBody(), xSharerUserId);
        return response;
    }

    @Override
    public ResponseEntity<List<ItemDTO>> searchItems(Long xSharerUserId, String search) {
        log.info("Запрос на поиск items={} для пользователя с id={}", search, xSharerUserId);
        ResponseEntity<List<ItemDTO>> response = client.searchItems(xSharerUserId, search);
        log.info("Найдены items={} для пользователя с id={}", response.getBody(), xSharerUserId);
        return response;
    }

    @Override
    public ResponseEntity<ItemDTO> updateItem(Long xSharerUserId, Long itemId, ItemDTO itemDTO) {
        log.info("Запрос на обновление item={} для пользователя с id={}", itemId, xSharerUserId);
        ResponseEntity<ItemDTO> response = client.updateItem(xSharerUserId, itemId, itemDTO);
        log.info("Обновлен item={} для пользователя с id={}", response.getBody(), xSharerUserId);
        return response;
    }

    @Override
    public ResponseEntity<CommentResponseDTO> createComment(Long xSharerUserId, Long itemId,
                                                            CommentRequestDTO commentRequestDTO) {
        log.info("Запрос на создание комментария к item={} для пользователя с id={}", itemId, xSharerUserId);
        ResponseEntity<CommentResponseDTO> response = client.createComment(xSharerUserId, itemId, commentRequestDTO);
        log.info("Создан комментарий к item={} для пользователя с id={}", itemId, xSharerUserId);
        return response;
    }

    private void checkCreatedItem(ItemDTO itemDTO) {
        if (itemDTO.getAvailable() == null
                || itemDTO.getName() == null
                || itemDTO.getName().isBlank()
                || itemDTO.getDescription() == null
                || itemDTO.getDescription().isBlank()) {
            throw new BadRequestException("Необходимо заполнить все обязательные поля");
        }
    }
}
