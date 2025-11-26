package com.example.curs4.service;

import com.example.curs4.dto.VehicleDTO;
import com.example.curs4.entity.User;
import com.example.curs4.entity.Vehicle;
import com.example.curs4.exception.CustomException;
import com.example.curs4.mapper.VehicleMapper;
import com.example.curs4.repository.UserRepository;
import com.example.curs4.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    // CREATE
    public VehicleDTO createVehicle(VehicleDTO dto) {
        log.info("Создание транспорта для клиента ID: {}", dto.getClientId());

        validateVehicle(dto);

        // Проверяем уникальность госномера
        if (vehicleRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new CustomException("Транспорт с номером " + dto.getLicensePlate() + " уже существует");
        }

        User client = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new CustomException("Клиент не найден"));

        Vehicle vehicle = vehicleMapper.toEntity(dto);
        vehicle.setClient(client);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Транспорт создан: {}", savedVehicle.getLicensePlate());

        return vehicleMapper.toDto(savedVehicle);
    }

    // READ
    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Транспорт не найден"));
        return vehicleMapper.toDto(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByClientId(Long clientId) {
        return vehicleRepository.findByClientId(clientId).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByType(String vehicleType) {
        return vehicleRepository.findByVehicleTypeContainingIgnoreCase(vehicleType).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleDTO getVehicleByLicensePlate(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new CustomException("Транспорт с номером " + licensePlate + " не найден"));
        return vehicleMapper.toDto(vehicle);
    }

    // UPDATE
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Транспорт не найден"));

        // Проверяем уникальность госномера (если изменился)
        if (!existingVehicle.getLicensePlate().equals(dto.getLicensePlate()) &&
                vehicleRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new CustomException("Транспорт с номером " + dto.getLicensePlate() + " уже существует");
        }

        // Обновляем поля
        existingVehicle.setLicensePlate(dto.getLicensePlate());
        existingVehicle.setModel(dto.getModel());
        existingVehicle.setVehicleType(dto.getVehicleType());
        existingVehicle.setYearOfManufacture(dto.getYearOfManufacture());
        existingVehicle.setCapacity(dto.getCapacity());

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        log.info("Транспорт обновлен: {}", updatedVehicle.getLicensePlate());

        return vehicleMapper.toDto(updatedVehicle);
    }

    // DELETE
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Транспорт не найден"));

        vehicleRepository.delete(vehicle);
        log.info("Транспорт удален: {}", vehicle.getLicensePlate());
    }

    // VALIDATION
    private void validateVehicle(VehicleDTO dto) {
        if (dto.getClientId() == null) {
            throw new CustomException("ID клиента обязателен");
        }

        if (dto.getLicensePlate() == null || dto.getLicensePlate().trim().isEmpty()) {
            throw new CustomException("Госномер обязателен");
        }
    }

    // STATISTICS
    @Transactional(readOnly = true)
    public long getVehiclesCountByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new CustomException("Клиент не найден"));
        return vehicleRepository.countByClient(client);
    }

    @Transactional(readOnly = true)
    public long getTrucksCountByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new CustomException("Клиент не найден"));
        return vehicleRepository.countTrucksByClient(client);
    }

    @Transactional(readOnly = true)
    public double getTotalCapacityByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new CustomException("Клиент не найден"));
        return vehicleRepository.getTotalCapacityByClient(client);
    }

    // UTILITY METHODS
    @Transactional(readOnly = true)
    public boolean vehicleExists(Long id) {
        return vehicleRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean licensePlateExists(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }
}