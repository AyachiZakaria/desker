package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    @NotBlank(message = "Desk ID is required")
    private String deskId;
    
    @NotBlank(message = "Date is required (yyyy-MM-dd)")
    private String date;
}
