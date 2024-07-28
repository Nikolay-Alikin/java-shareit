package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.ItemDTO;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestEntity", ignore = true)
    ItemEntity toEntity(ItemDTO itemDTO);

    List<ItemEntity> toEntity(List<ItemDTO> dtos);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "requestEntity.id")
    ItemDTO toDto(ItemEntity entity);

    List<ItemDTO> toDto(List<ItemEntity> dtos);

    default void updateItem(ItemEntity entity, ItemDTO dto) {
        entity.setDescription(dto.getDescription() == null ? entity.getDescription() : dto.getDescription());
        entity.setName(dto.getName() == null ? entity.getName() : dto.getName());
        entity.setAvailable(dto.getAvailable() == null ? entity.getAvailable() : dto.getAvailable());
    }
}
