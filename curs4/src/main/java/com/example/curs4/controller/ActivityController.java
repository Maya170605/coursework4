package com.example.curs4.controller;

import com.example.curs4.dto.ActivityDTO;
import com.example.curs4.exception.CustomException;
import com.example.curs4.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "Создать активность")
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO dto) {
        log.info("Создание активности для пользователя ID: {}", dto.getUserId());
        ActivityDTO saved = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Создать активность по имени пользователя")
    @PostMapping("/user/{username}")
    public ResponseEntity<ActivityDTO> createActivityForUser(
            @PathVariable String username,
            @RequestBody Map<String, String> request) {

        String description = request.get("description");
        log.info("Создание активности для пользователя: {}", username);

        ActivityDTO saved = activityService.createActivityForUser(username, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Получить активность по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        log.info("Получение активности по ID: {}", id);
        ActivityDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @Operation(summary = "Получить все активности")
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        log.info("Получение всех активностей");
        List<ActivityDTO> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Получить активности по пользователю")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByUser(@PathVariable Long userId) {
        log.info("Получение активностей для пользователя ID: {}", userId);
        List<ActivityDTO> activities = activityService.getActivitiesByUserId(userId);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Получить последние активности пользователя")
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<ActivityDTO>> getRecentActivitiesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {

        log.info("Получение {} последних активностей пользователя ID: {}", limit, userId);
        List<ActivityDTO> activities = activityService.getRecentActivitiesByUserId(userId, limit);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Получить активности пользователя с пагинацией")
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<ActivityDTO>> getActivitiesByUserWithPagination(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Получение активностей пользователя ID: {} - страница: {}, размер: {}", userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityDTO> activities = activityService.getActivitiesByUserId(userId, pageable);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Получить активности по диапазону дат")
    @GetMapping("/date-range")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Получение активностей с {} по {}", startDate, endDate);
        List<ActivityDTO> activities = activityService.getActivitiesByDateRange(startDate, endDate);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Получить активности пользователя по диапазону дат")
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Получение активностей пользователя ID: {} с {} по {}", userId, startDate, endDate);
        List<ActivityDTO> activities = activityService.getActivitiesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Поиск активностей пользователя по ключевому слову")
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(
            @PathVariable Long userId,
            @RequestParam String keyword) {

        log.info("Поиск активностей пользователя ID: {} по ключевому слову: {}", userId, keyword);
        List<ActivityDTO> activities = activityService.searchActivitiesByUserAndKeyword(userId, keyword);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Обновить активность")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id,
                                                      @Valid @RequestBody ActivityDTO dto) {
        log.info("Обновление активности ID: {}", id);
        ActivityDTO updated = activityService.updateActivity(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить активность")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        log.info("Удаление активности ID: {}", id);
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить все активности пользователя")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllActivitiesByUser(@PathVariable Long userId) {
        log.info("Удаление всех активностей пользователя ID: {}", userId);
        activityService.deleteAllActivitiesByUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить статистику по пользователю")
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        log.info("Получение статистики активностей для пользователя ID: {}", userId);

        long totalActivities = activityService.getActivitiesCountByUser(userId);
        long todayActivities = activityService.getTodayActivitiesCountByUser(userId);

        return ResponseEntity.ok(Map.of(
                "totalActivities", totalActivities,
                "todayActivities", todayActivities
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