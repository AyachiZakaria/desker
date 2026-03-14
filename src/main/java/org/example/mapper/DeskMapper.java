package org.example.mapper;

import org.example.dto.DeskDto;
import org.example.dto.CreateDeskRequest;
import org.example.model.Desk;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeskMapper {
    DeskMapper INSTANCE = Mappers.getMapper(DeskMapper.class);

    DeskDto toDto(Desk desk);
    Desk toEntity(DeskDto deskDto);
    Desk toEntity(CreateDeskRequest request);
    List<DeskDto> toDtoList(List<Desk> desks);
}
