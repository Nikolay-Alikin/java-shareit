package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.entity.ItemEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.ItemDTO;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemMapper {

    @Mapping(target = "owner", ignore = true)
    ItemEntity toEntity(ItemDTO itemDTO);

    List<ItemEntity> toEntity(List<ItemDTO> dtos);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemDTO toDto(ItemEntity itemDTO);

    List<ItemDTO> toDto(List<ItemEntity> dtos);

    default void updateItem(ItemEntity entity, ItemDTO dto) {
        entity.setDescription(dto.getDescription() == null ? entity.getDescription() : dto.getDescription());
        entity.setName(dto.getName() == null ? entity.getName() : dto.getName());
        entity.setAvailable(dto.getAvailable() == null ? entity.getAvailable() : dto.getAvailable());
        entity.setRequest(dto.getRequest() == null ? entity.getRequest() : dto.getRequest());
    }
}