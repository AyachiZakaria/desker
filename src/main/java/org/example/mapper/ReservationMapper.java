package org.example.mapper;

import org.example.dto.ReservationDto;
import org.example.dto.ReservationRequest;
import org.example.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    ReservationDto toDto(Reservation reservation);
    Reservation toEntity(ReservationDto reservationDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true) // to be set by service
    @Mapping(target = "username", ignore = true) // to be set by service
    @Mapping(target = "createdAt", ignore = true) // to be set by service
    @Mapping(target = "deskName", ignore = true) // to be set by service
    Reservation toEntity(ReservationRequest request);

    List<ReservationDto> toDtoList(List<Reservation> reservations);
}
