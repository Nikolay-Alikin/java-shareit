package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.CommentResponseDTO;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "entity.userEntity.name")
    CommentResponseDTO toResponse(CommentEntity entity);
}
