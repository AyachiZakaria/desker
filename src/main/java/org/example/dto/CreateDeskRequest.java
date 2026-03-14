package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeskRequest {

    @NotBlank
    private String name;

    @NotNull
    private Double positionX;

    @NotNull
    private Double positionY;

    @NotNull
    private Integer floor;

    private String zone;
}

