package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ReservationDto;
import org.example.mapper.ReservationMapper;
import org.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public List<ReservationDto> getReservationsByMonth(int year, int month) throws ExecutionException, InterruptedException {
        String yearMonthPrefix = String.format("%04d-%02d", year, month);
        return reservationMapper.toDtoList(reservationRepository.findByMonth(yearMonthPrefix));
    }

    public Map<String, Long> getMonthlyDeskUsageReport(int year, int month) throws ExecutionException, InterruptedException {
        String yearMonthPrefix = String.format("%04d-%02d", year, month);
        List<org.example.model.Reservation> reservations = reservationRepository.findByMonth(yearMonthPrefix);
        
        // Return a map of deskId -> count of reservations
        return reservations.stream()
                .collect(Collectors.groupingBy(
                        org.example.model.Reservation::getDeskId,
                        Collectors.counting()
                ));
    }
}
