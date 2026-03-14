package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    private String id;
    private String userId;
    private String deskId;
    
    // Using String for Firestore compatibility with simple queries (yyyy-MM-dd)
    private String date;
    
    // Storing exact booking timestamp
    private Long createdAt;
}
