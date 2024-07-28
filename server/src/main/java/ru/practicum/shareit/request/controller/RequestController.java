package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.RequestsApi;
import ru.yandex.practicum.generated.model.dto.RequestDTO;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestController implements RequestsApi {

    private final RequestService requestService;

    @Override
    public ResponseEntity<RequestResponseDTO> createRequest(Long xSharerUserId, RequestDTO requestDTO) {
        log.info("xSharerUserId={} Поступил запрос на создание запроса: {}", xSharerUserId, requestDTO);
        RequestResponseDTO request = requestService.createRequest(xSharerUserId, requestDTO);
        log.info("xSharerUserId ={} Запрос создан: {}", xSharerUserId, request);
        return ResponseEntity.status(200).body(request);
    }

    @Override
    public ResponseEntity<List<RequestResponseDTO>> getAllRequests(Long xSharerUserId, Integer from, Integer size) {
        log.info("xSharerUserId={} Поступил запрос на получение всех реквестов: from={}, size={}", xSharerUserId, from,
                size);
        List<RequestResponseDTO> requests = requestService.getAllRequests(xSharerUserId, from, size);
        log.info("xSharerUserId ={} Запросы получены: {}", xSharerUserId, requests);
        return ResponseEntity.ok(requests);
    }

    @Override
    public ResponseEntity<RequestResponseDTO> getRequest(Long xSharerUserId, Long requestId) {
        log.info("xSharerUserId={} Поступил запрос на получение реквеста: {}", xSharerUserId, requestId);
        RequestResponseDTO request = requestService.getRequest(xSharerUserId, requestId);
        log.info("xSharerUserId ={} реквест получен: {}", xSharerUserId, request);
        return ResponseEntity.ok(request);
    }

    @Override
    public ResponseEntity<List<RequestResponseDTO>> getRequests(Long xSharerUserId) {
        log.info("xSharerUserId={} Поступил запрос на получение всех реквестов", xSharerUserId);
        List<RequestResponseDTO> requests = requestService.getRequests(xSharerUserId);
        log.info("xSharerUserId ={} реквесты получены: {}", xSharerUserId, requests);
        return ResponseEntity.ok(requests);
    }
}
