package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.feign.RequestClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.RequestsApi;
import ru.yandex.practicum.generated.model.dto.RequestDTO;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestController implements RequestsApi {

    private final RequestClient client;

    @Override
    public ResponseEntity<RequestResponseDTO> createRequest(Long xSharerUserId, RequestDTO requestDTO) {
        log.info("xSharerUserId={} Поступил запрос на создание запроса: {}", xSharerUserId, requestDTO);
        ResponseEntity<RequestResponseDTO> response = client.createRequest(xSharerUserId, requestDTO);
        log.info("xSharerUserId ={} Запрос создан: {}", xSharerUserId, response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<RequestResponseDTO>> getAllRequests(Long xSharerUserId, Integer from, Integer size) {
        log.info("xSharerUserId={} Поступил запрос на получение всех реквестов: from={}, size={}", xSharerUserId, from,
                size);
        ResponseEntity<List<RequestResponseDTO>> response = client.getAllRequests(xSharerUserId, from, size);
        log.info("xSharerUserId ={} Запросы получены: {}", xSharerUserId, response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<RequestResponseDTO> getRequest(Long xSharerUserId, Long requestId) {
        log.info("xSharerUserId={} Поступил запрос на получение реквеста: {}", xSharerUserId, requestId);
        ResponseEntity<RequestResponseDTO> response = client.getRequest(xSharerUserId, requestId);
        log.info("xSharerUserId ={} реквест получен: {}", xSharerUserId, response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<RequestResponseDTO>> getRequests(Long xSharerUserId) {
        log.info("xSharerUserId={} Поступил запрос на получение всех реквестов", xSharerUserId);
        ResponseEntity<List<RequestResponseDTO>> response = client.getRequests(xSharerUserId);
        log.info("xSharerUserId ={} реквесты получены: {}", xSharerUserId, response.getBody());
        return response;
    }
}
