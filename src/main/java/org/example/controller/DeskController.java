package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.CreateDeskRequest;
import org.example.dto.DeskDto;
import org.example.service.DeskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/desks")
@RequiredArgsConstructor
@Tag(name = "Desk API", description = "Endpoints for retrieving desks and their availability")
public class DeskController {

    private final DeskService deskService;

    @GetMapping
    @Operation(summary = "Get all desks", description = "Retrieves a list of all desks in the office")
    public ResponseEntity<List<DeskDto>> getAllDesks() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.getAllDesks());
    }

    @GetMapping("/availability")
    @Operation(summary = "Get available desks by date", description = "Retrieves a list of available desks for a specific date (yyyy-MM-dd)")
    public ResponseEntity<List<DeskDto>> getAvailableDesks(@RequestParam String date) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.getAvailableDesks(date));
    }

    @PostMapping
    @Operation(summary = "Add a new desk", description = "Creates a new desk and returns it (Firestore id is generated automatically)")
    public ResponseEntity<DeskDto> addDesk(@Valid @RequestBody CreateDeskRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.createDesk(request));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Add multiple desks", description = "Creates multiple desks in a single request (Firestore ids are generated automatically)")
    public ResponseEntity<List<DeskDto>> addDesksBulk(@Valid @RequestBody List<CreateDeskRequest> requests) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.createDesks(requests));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a desk by id", description = "Replaces desk fields by Firestore id and returns the updated desk")
    public ResponseEntity<DeskDto> updateDeskById(@PathVariable String id, @Valid @RequestBody CreateDeskRequest request)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.updateDeskById(id, request));
    }

    @PutMapping("/by-name/{name}")
    @Operation(summary = "Update a desk by name", description = "Updates a desk by name (requires the name to be unique) and returns the updated desk")
    public ResponseEntity<DeskDto> updateDeskByName(@PathVariable String name, @Valid @RequestBody CreateDeskRequest request)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(deskService.updateDeskByName(name, request));
    }
}
