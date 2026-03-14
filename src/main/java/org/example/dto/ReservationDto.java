package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private String id;
    private String userId;
    private String username;
    private String deskId;
    private String deskName;
    private String date;
    private Long createdAt;
}
