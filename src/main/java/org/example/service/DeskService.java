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
import java.util.Optional;
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

        // Persist each desk so Firestore generates ids (and we return them to the caller)
        List<Desk> saved = new java.util.ArrayList<>(desks.size());
        for (Desk desk : desks) {
            saved.add(deskRepository.save(desk));
        }

        return deskMapper.toDtoList(saved);
    }

    public DeskDto updateDeskById(String id, CreateDeskRequest request) throws ExecutionException, InterruptedException {
        Optional<Desk> existingOpt = deskRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Desk not found: " + id);
        }

        Desk updated = deskMapper.toEntity(request);
        updated.setId(id);
        Desk saved = deskRepository.save(updated);
        return deskMapper.toDto(saved);
    }

    /**
     * Updates a desk by name. Requires exactly one desk with the given name.
     */
    public DeskDto updateDeskByName(String name, CreateDeskRequest request) throws ExecutionException, InterruptedException {
        List<Desk> matches = deskRepository.findByName(name);
        if (matches.isEmpty()) {
            throw new IllegalArgumentException("Desk not found with name: " + name);
        }
        if (matches.size() > 1) {
            throw new IllegalArgumentException("Desk name is not unique: " + name);
        }

        return updateDeskById(matches.get(0).getId(), request);
    }
}
