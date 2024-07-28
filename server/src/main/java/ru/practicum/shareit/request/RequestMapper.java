package ru.practicum.shareit.request;

import ru.practicum.shareit.request.entity.RequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.RequestResponseDTO;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RequestMapper {

    @Mapping(target = "items", ignore = true)
    RequestResponseDTO toRequestResponseDTO(RequestEntity requestEntity);

}
