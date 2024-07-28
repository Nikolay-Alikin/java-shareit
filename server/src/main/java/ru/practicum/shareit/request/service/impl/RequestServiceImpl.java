package ru.practicum.shareit.request.service.impl;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.entity.RequestEntity;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.generated.model.dto.RequestDTO;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;
import ru.yandex.practicum.generated.model.dto.RequestedItemsDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserService userService;
    private final RequestMapper requestMapper;
    private final RequestJpaRepository repository;
    private final ItemJpaRepository itemRepository;
    private final RequestJpaRepository requestJpaRepository;

    @Override
    @Transactional
    public RequestResponseDTO createRequest(Long xSharerUserId, RequestDTO requestDTO) {
        return requestMapper.toRequestResponseDTO(repository.save(
                new RequestEntity()
                        .setRequestor(userService.getUserEntity(xSharerUserId))
                        .setDescription(requestDTO.getDescription())
        ));
    }

    @Override
    public List<RequestResponseDTO> getAllRequests(Long xSharerUserId, Integer from, Integer size) {
        int pageNumber = from / size;
        PageRequest pageRequest = PageRequest.of(pageNumber, size, Sort.by("created").ascending());

        Map<Long, RequestResponseDTO> requests = requestJpaRepository.findAll(pageRequest).stream()
                .collect(Collectors.toMap(RequestEntity::getId, requestMapper::toRequestResponseDTO));

        setItems(requests);
        return requests.values().stream().toList();
    }


    @Override
    public RequestResponseDTO getRequest(Long xSharerUserId, Long requestId) {
        RequestEntity requestEntity = findRequestEntity(requestId);
        List<ItemEntity> items = itemRepository.findItemEntitiesByRequestEntityId(requestId);
        RequestResponseDTO requestResponseDTO = requestMapper.toRequestResponseDTO(requestEntity);
        requestResponseDTO.setItems(items.stream()
                .map(item -> new RequestedItemsDTO()
                        .id(item.getId())
                        .name(item.getName())
                        .ownerId(item.getOwner().getId())).toList());
        return requestResponseDTO;
    }

    @Override
    public List<RequestResponseDTO> getRequests(Long xSharerUserId) {
        Map<Long, RequestResponseDTO> responseDTOS = repository.findAllByRequestorIdOrderByCreatedAsc(xSharerUserId)
                .stream()
                .map(requestMapper::toRequestResponseDTO)
                .collect(Collectors.toMap(RequestResponseDTO::getId, dto -> dto));

        setItems(responseDTOS);
        return responseDTOS.values().stream().toList();
    }

    private void setItems(Map<Long, RequestResponseDTO> requests) {
        List<ItemEntity> items = itemRepository.findItemEntitiesByRequestEntityIdIn(requests.keySet());
        items.forEach(item -> {
            Long requestId = item.getRequestEntity().getId();
            requests.get(requestId).getItems().add(new RequestedItemsDTO()
                    .id(item.getId())
                    .name(item.getName())
                    .ownerId(item.getOwner().getId()));
        });
    }

    private RequestEntity findRequestEntity(Long requestId) {
        return repository.findById(requestId).orElseThrow(() -> {
            log.error("Реквест с id={} не найден", requestId);
            return new NotFoundException("Htrdtcn с id " + requestId + " не найден");
        });
    }
}
