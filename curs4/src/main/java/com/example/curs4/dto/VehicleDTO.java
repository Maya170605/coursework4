package com.example.curs4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private Long id;

    @NotBlank(message = "Госномер обязателен")
    private String licensePlate;

    private String model;

    private String vehicleType;

    private Integer yearOfManufacture;

    private Double capacity;

    @NotNull(message = "Client ID обязателен")
    private Long clientId;

    private String clientName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}