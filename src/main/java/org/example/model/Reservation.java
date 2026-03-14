package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    private String id;
    private String userId;
    /**
     * Denormalized username (currently derived from Firebase email) stored for convenient reads.
     * May be null for older documents.
     */
    private String username;
    private String deskId;
    /**
     * Denormalized desk name stored for convenient reads.
     * May be null for older documents.
     */
    private String deskName;

    // Using String for Firestore compatibility with simple queries (yyyy-MM-dd)
    private String date;
    
    // Storing exact booking timestamp
    private Long createdAt;
}
