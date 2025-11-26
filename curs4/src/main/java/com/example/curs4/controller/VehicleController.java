package com.example.curs4.controller;

import com.example.curs4.dto.VehicleDTO;
import com.example.curs4.exception.CustomException;
import com.example.curs4.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Создать транспорт")
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO dto) {
        log.info("Создание транспорта для клиента ID: {}", dto.getClientId());
        VehicleDTO saved = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Получить транспорт по ID")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        log.info("Получение транспорта по ID: {}", id);
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(summary = "Получить все транспортные средства")
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        log.info("Получение всех транспортных средств");
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Получить транспорт по клиенту")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByClient(@PathVariable Long clientId) {
        log.info("Получение транспорта для клиента ID: {}", clientId);
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByClientId(clientId);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Получить транспорт по типу")
    @GetMapping("/type/{vehicleType}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByType(@PathVariable String vehicleType) {
        log.info("Получение транспорта по типу: {}", vehicleType);
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByType(vehicleType);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Получить транспорт по госномеру")
    @GetMapping("/license-plate/{licensePlate}")
    public ResponseEntity<VehicleDTO> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        log.info("Получение транспорта по госномеру: {}", licensePlate);
        VehicleDTO vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(summary = "Обновить транспорт")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id,
                                                    @Valid @RequestBody VehicleDTO dto) {
        log.info("Обновление транспорта ID: {}", id);
        VehicleDTO updated = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить транспорт")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.info("Удаление транспорта ID: {}", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Проверить существование госномера")
    @GetMapping("/check-license-plate/{licensePlate}")
    public ResponseEntity<Map<String, Boolean>> checkLicensePlateExists(@PathVariable String licensePlate) {
        log.info("Проверка госномера: {}", licensePlate);
        boolean exists = vehicleService.licensePlateExists(licensePlate);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @Operation(summary = "Получить статистику по клиенту")
    @GetMapping("/client/{clientId}/stats")
    public ResponseEntity<Map<String, Object>> getClientStats(@PathVariable Long clientId) {
        log.info("Получение статистики транспорта для клиента ID: {}", clientId);

        long totalVehicles = vehicleService.getVehiclesCountByClient(clientId);
        long trucksCount = vehicleService.getTrucksCountByClient(clientId);
        double totalCapacity = vehicleService.getTotalCapacityByClient(clientId);

        return ResponseEntity.ok(Map.of(
                "totalVehicles", totalVehicles,
                "trucksCount", trucksCount,
                "totalCapacity", totalCapacity
        ));
    }

    // Exception Handlers
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        log.error("Ошибка: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Внутренняя ошибка сервера", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка сервера");
    }
}