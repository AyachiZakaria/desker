package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ReservationDto;
import org.example.dto.ReservationRequest;
import org.example.mapper.ReservationMapper;
import org.example.model.Desk;
import org.example.model.Reservation;
import org.example.repository.DeskRepository;
import org.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DeskRepository deskRepository;
    private final ReservationMapper reservationMapper;

    public List<ReservationDto> getMyReservations(String userId) throws ExecutionException, InterruptedException {
        return reservationMapper.toDtoList(reservationRepository.findByUserId(userId));
    }

    public List<ReservationDto> reserveDesks(String userId, List<ReservationRequest> requests) throws ExecutionException, InterruptedException {
        // Group by month to validate rules per month
        // Assuming user submits reservations for a single month at a time for simplicity
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Reservation request list cannot be empty");
        }

        String sampleDateStr = requests.get(0).getDate();
        LocalDate sampleDate = LocalDate.parse(sampleDateStr);
        String yearMonthPrefix = sampleDateStr.substring(0, 7); // e.g. "2026-03"

        // Fetch existing reservations for the month
        List<Reservation> existingReservations = reservationRepository.findByUserIdAndMonth(userId, yearMonthPrefix);

        // Convert new requests to entities
        List<Reservation> newReservations = requests.stream().map(req -> {
            Reservation res = reservationMapper.toEntity(req);
            res.setUserId(userId);
            res.setCreatedAt(System.currentTimeMillis());
            return res;
        }).collect(Collectors.toList());

        // Combine for validation
        List<Reservation> allMonthlyReservations = new java.util.ArrayList<>(existingReservations);
        allMonthlyReservations.addAll(newReservations);

        validateMonthlyRules(allMonthlyReservations, yearMonthPrefix);

        // Validate double booking per Request
        for (ReservationRequest req : requests) {
            List<Reservation> conflicting = reservationRepository.findByDeskIdAndDate(req.getDeskId(), req.getDate());
            if (!conflicting.isEmpty()) {
                throw new IllegalArgumentException("Desk " + req.getDeskId() + " is already reserved on " + req.getDate());
            }
            // Check if desk exists
            Optional<Desk> deskOpt = deskRepository.findById(req.getDeskId());
            if (deskOpt.isEmpty()) {
                throw new IllegalArgumentException("Desk " + req.getDeskId() + " does not exist");
            }
            // Check if user already booked a desk on this date
            boolean userAlreadyBooked = existingReservations.stream().anyMatch(r -> r.getDate().equals(req.getDate()));
            if(userAlreadyBooked) {
                throw new IllegalArgumentException("User already has a desk booked on " + req.getDate());
            }
        }

        // Save new reservations
        for (Reservation res : newReservations) {
            reservationRepository.save(res);
        }

        return reservationMapper.toDtoList(newReservations);
    }

    private void validateMonthlyRules(List<Reservation> reservations, String yearMonthPrefix) {
        int year = Integer.parseInt(yearMonthPrefix.substring(0, 4));
        int month = Integer.parseInt(yearMonthPrefix.substring(5, 7));

        long fridaysCount = 0;
        int[] daysPerWeek = new int[6]; // Up to 6 weeks in a month

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Reservation res : reservations) {
            LocalDate date = LocalDate.parse(res.getDate());
            if (date.getYear() != year || date.getMonthValue() != month) {
                continue; // Skip if somehow out of month
            }
            
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                fridaysCount++;
            }

            // Calculate week of month index
            int weekOfMonth = date.get(weekFields.weekOfMonth());
            if (weekOfMonth >= 1 && weekOfMonth <= 6) {
                daysPerWeek[weekOfMonth - 1]++;
            }
        }

        if (fridaysCount < 2) {
            throw new IllegalArgumentException("Must reserve at least 2 Fridays per month. Currently: " + fridaysCount);
        }

        // Validate minimum 3 days per week ON WEEKS WHERE THE USER IS BOOKING AT LEAST 1 DAY
        // Or strictly 3 days every week of the month? Let's assume strict 3 days per week if there are 5 business days.
        // For simplicity: if a user books anything in a week, they must book at least 3 days.
        for (int i = 0; i < daysPerWeek.length; i++) {
            if (daysPerWeek[i] > 0 && daysPerWeek[i] < 3) {
                // In a real scenario we'd check if the week has 3 working days at all,
                // but we will simplify and throw the error.
                throw new IllegalArgumentException("Minimum 3 days per week onsite is required. Week " + (i+1) + " has " + daysPerWeek[i]);
            }
        }
    }
}
