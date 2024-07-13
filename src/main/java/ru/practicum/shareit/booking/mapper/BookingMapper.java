package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.entity.BookingEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.BookingDTO;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingMapper {


    @Mapping(target = "booker.id", source = "userEntity.id")
    @Mapping(target = "item.id", source = "itemEntity.id")
    @Mapping(target = "item.name", source = "itemEntity.name")
    BookingDTO toDto(BookingEntity entity);

    @Mapping(target = "booker.id", source = "userEntity.id")
    @Mapping(target = "item.id", source = "itemEntity.id")
    @Mapping(target = "item.name", source = "itemEntity.name")
    List<BookingDTO> toDto(List<BookingEntity> entities);
}
