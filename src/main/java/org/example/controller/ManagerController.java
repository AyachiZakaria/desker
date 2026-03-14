package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.ReservationDto;
import org.example.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Manager API", description = "Endpoints for managers to view reports and all reservations")
public class ManagerController {

    private final ReportService reportService;

    @GetMapping("/reservations/month/{year}/{month}")
    @Operation(summary = "Get all reservations by month", description = "Retrieves all desk reservations for a specific month. Manager only.")
    public ResponseEntity<List<ReservationDto>> getReservationsByMonth(@PathVariable int year, @PathVariable int month) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(reportService.getReservationsByMonth(year, month));
    }

    @GetMapping("/reports/month/{year}/{month}")
    @Operation(summary = "Get desk usage report by month", description = "Retrieves an aggregated usage report of all desks for a specific month. Manager only.")
    public ResponseEntity<Map<String, Long>> getMonthlyReport(@PathVariable int year, @PathVariable int month) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(reportService.getMonthlyDeskUsageReport(year, month));
    }
}
