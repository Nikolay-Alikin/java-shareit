package ru.practicum.shareit.request.service;

import ru.yandex.practicum.generated.model.dto.RequestDTO;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;

import java.util.List;

public interface RequestService {

    RequestResponseDTO createRequest(Long xSharerUserId, RequestDTO requestDTO);

    List<RequestResponseDTO> getAllRequests(Long xSharerUserId, Integer from, Integer size);

    RequestResponseDTO getRequest(Long xSharerUserId, Long requestId);

    List<RequestResponseDTO> getRequests(Long xSharerUserId);
}
