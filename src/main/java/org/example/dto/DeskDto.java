package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskDto {
    private String id;
    private String name;
    private Double positionX;
    private Double positionY;
    private Integer floor;
    private String zone;
}
