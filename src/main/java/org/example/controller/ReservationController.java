package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ReservationDto;
import org.example.dto.ReservationRequest;
import org.example.security.FirebaseAuthenticationToken;
import org.example.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reservation API", description = "Endpoints for managing user reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/my-reservations")
    @Operation(summary = "Get my reservations", description = "Retrieves all reservations for the currently authenticated user")
    public ResponseEntity<List<ReservationDto>> getMyReservations(Principal principal) throws ExecutionException, InterruptedException {
        if (principal instanceof FirebaseAuthenticationToken token) {
            String uid = token.getUid();
            return ResponseEntity.ok(reservationService.getMyReservations(uid));
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/reservations")
    @Operation(summary = "Create reservations", description = "Creates desk reservations for a month. Validates min 3 days/week and 2 Fridays/month.")
    public ResponseEntity<?> reserveDesks(@RequestBody @Valid List<ReservationRequest> requests, Principal principal) {
        try {
            if (principal instanceof FirebaseAuthenticationToken token) {
                String uid = token.getUid();
                List<ReservationDto> created = reservationService.reserveDesks(uid, requests);
                return ResponseEntity.ok(created);
            }
            return ResponseEntity.status(401).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
