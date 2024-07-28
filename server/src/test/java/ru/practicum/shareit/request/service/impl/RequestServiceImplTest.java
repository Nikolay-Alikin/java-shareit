package ru.practicum.shareit.request.service.impl;

import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.entity.RequestEntity;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.generated.model.dto.ItemDTO;
import ru.yandex.practicum.generated.model.dto.RequestDTO;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;
import ru.yandex.practicum.generated.model.dto.RequestedItemsDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DisplayName("Тестирование класса RequestServiceImpl")
@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    private final RequestDTO requestDtoStub = new RequestDTO()
            .description("Тестовый запрос предмета");
    private final UserEntity userEntity = new UserEntity()
            .setId(1L)
            .setName("Тестовый пользователь")
            .setEmail("email@ru.ru");
    private final UserEntity requestor = new UserEntity()
            .setId(2L)
            .setName("Тестовый пользователь")
            .setEmail("email@ru.ru");
    private final RequestEntity requestEntity = new RequestEntity()
            .setId(5L)
            .setDescription("Тестовое описание")
            .setRequestor(requestor)
            .setCreated(LocalDateTime.now());
    private final ItemDTO itemDTO = new ItemDTO()
            .name("Тестовый предмет")
            .description("Тестовое описание")
            .available(true);

    private final ItemEntity itemEntity = new ItemEntity()
            .setId(1L)
            .setOwner(userEntity)
            .setName(itemDTO.getName())
            .setDescription(itemDTO.getDescription())
            .setAvailable(true);


    @Mock
    private UserService userService;
    @Mock
    private RequestJpaRepository requestJpaRepository;
    @Mock
    private ItemJpaRepository itemJpaRepository;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @InjectMocks
    private RequestServiceImpl requestService;


    @Test
    @DisplayName("Test create request")
    void createRequest() {
        RequestResponseDTO expectedResult = new RequestResponseDTO()
                .created(requestEntity.getCreated())
                .id(requestEntity.getId())
                .description(requestEntity.getDescription());

        Mockito.when(userService.getUserEntity(1L))
                .thenReturn(requestor);
        Mockito.when(requestJpaRepository.save(Mockito.any(RequestEntity.class)))
                .thenReturn(requestEntity);

        RequestResponseDTO result = requestService.createRequest(1L, requestDtoStub);

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test get all requests")
    void getAllRequests() {
        itemEntity.setRequestEntity(requestEntity);
        RequestResponseDTO expectedResult = new RequestResponseDTO()
                .id(5L)
                .description(requestEntity.getDescription())
                .created(requestEntity.getCreated())
                .items(List.of(new RequestedItemsDTO()
                        .id(itemEntity.getId())
                        .name(itemEntity.getName())
                        .ownerId(itemEntity.getOwner().getId())));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("created").ascending());

        Mockito.when(requestJpaRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(List.of(requestEntity)));
        Mockito.when(itemJpaRepository.findItemEntitiesByRequestEntityIdIn(Set.of(requestEntity.getId())))
                .thenReturn(List.of(itemEntity));

        List<RequestResponseDTO> allRequests = requestService.getAllRequests(1L, 0, 10);

        Assertions.assertThat(allRequests.getFirst())
                .isEqualTo(expectedResult);

    }

    @Test
    @DisplayName("Test get request")
    void getRequest() {
        itemEntity.setRequestEntity(requestEntity);
        RequestResponseDTO expectedResult = new RequestResponseDTO()
                .id(5L)
                .description(requestEntity.getDescription())
                .created(requestEntity.getCreated())
                .items(List.of(new RequestedItemsDTO()
                        .id(itemEntity.getId())
                        .name(itemEntity.getName())
                        .ownerId(itemEntity.getOwner().getId())));

        Mockito.when(requestJpaRepository.findById(5L))
                .thenReturn(Optional.of(requestEntity));
        Mockito.when(itemJpaRepository.findItemEntitiesByRequestEntityId(requestEntity.getId()))
                .thenReturn(List.of(itemEntity));

        RequestResponseDTO result = requestService.getRequest(1L, 5L);

        Assertions.assertThat(result)
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test get requests")
    void getRequests() {
        itemEntity.setRequestEntity(requestEntity);
        RequestResponseDTO expectedResult = new RequestResponseDTO()
                .id(5L)
                .description(requestEntity.getDescription())
                .created(requestEntity.getCreated())
                .items(List.of(new RequestedItemsDTO()
                        .id(itemEntity.getId())
                        .name(itemEntity.getName())
                        .ownerId(itemEntity.getOwner().getId())));
        Mockito.when(requestJpaRepository.findAllByRequestorIdOrderByCreatedAsc(1L))
                .thenReturn(List.of(requestEntity));
        Mockito.when(itemJpaRepository.findItemEntitiesByRequestEntityIdIn(Set.of(requestEntity.getId())))
                .thenReturn(List.of(itemEntity));

        List<RequestResponseDTO> result = requestService.getRequests(1L);

        Assertions.assertThat(result)
                .isEqualTo(List.of(expectedResult));
    }
}