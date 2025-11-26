package com.example.curs4.mapper;

import com.example.curs4.dto.VehicleDTO;
import com.example.curs4.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleDTO toDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleDTO.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .model(vehicle.getModel())
                .vehicleType(vehicle.getVehicleType())
                .yearOfManufacture(vehicle.getYearOfManufacture())
                .capacity(vehicle.getCapacity())
                .clientId(vehicle.getClient().getId())
                .clientName(vehicle.getClient().getName())
                .createdAt(vehicle.getCreatedAt())
                //.updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    public Vehicle toEntity(VehicleDTO dto) {
        if (dto == null) {
            return null;
        }

        return Vehicle.builder()
                .id(dto.getId())
                .licensePlate(dto.getLicensePlate())
                .model(dto.getModel())
                .vehicleType(dto.getVehicleType())
                .yearOfManufacture(dto.getYearOfManufacture())
                .capacity(dto.getCapacity())
                .build();
    }
}