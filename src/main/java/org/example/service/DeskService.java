package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateDeskRequest;
import org.example.dto.DeskDto;
import org.example.mapper.DeskMapper;
import org.example.model.Desk;
import org.example.repository.DeskRepository;
import org.example.repository.ReservationRepository;
import org.example.model.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final DeskRepository deskRepository;
    private final ReservationRepository reservationRepository;
    private final DeskMapper deskMapper;

    public List<DeskDto> getAllDesks() throws ExecutionException, InterruptedException {
        return deskMapper.toDtoList(deskRepository.findAll());
    }

    public List<DeskDto> getAvailableDesks(String date) throws ExecutionException, InterruptedException {
        List<Desk> allDesks = deskRepository.findAll();
        
        List<Reservation> reservationsOnDate = reservationRepository.findByDate(date);
        Set<String> reservedDeskIds = reservationsOnDate.stream()
                .map(Reservation::getDeskId)
                .collect(Collectors.toSet());
                
        List<Desk> availableDesks = allDesks.stream()
                .filter(desk -> !reservedDeskIds.contains(desk.getId()))
                .collect(Collectors.toList());
                
        return deskMapper.toDtoList(availableDesks);
    }

    public DeskDto createDesk(CreateDeskRequest request) throws ExecutionException, InterruptedException {
        Desk desk = deskMapper.toEntity(request);
        // Ensure Firestore auto-generates an id
        desk.setId(null);
        Desk saved = deskRepository.save(desk);
        return deskMapper.toDto(saved);
    }

    public List<DeskDto> createDesks(List<CreateDeskRequest> requests) throws ExecutionException, InterruptedException {
        List<Desk> desks = requests.stream()
                .map(deskMapper::toEntity)
                .peek(d -> d.setId(null))
                .toList();

        for (Desk desk : desks) {
            deskRepository.save(desk);
        }

        return deskMapper.toDtoList(desks);
    }
}
