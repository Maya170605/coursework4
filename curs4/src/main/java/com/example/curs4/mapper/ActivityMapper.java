package com.example.curs4.mapper;

import com.example.curs4.dto.ActivityDTO;
import com.example.curs4.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDTO toDto(Activity activity) {
        if (activity == null) {
            return null;
        }

        return ActivityDTO.builder()
                .id(activity.getId())
                .userId(activity.getUser().getId())
                .userName(activity.getUser().getName())
                .description(activity.getDescription())
                .activityDate(activity.getActivityDate())
                //.createdAt(activity.getCreatedAt())
                //.updatedAt(activity.getUpdatedAt())
                .build();
    }

    public Activity toEntity(ActivityDTO dto) {
        if (dto == null) {
            return null;
        }

        return Activity.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .activityDate(dto.getActivityDate() != null ? dto.getActivityDate() : java.time.LocalDateTime.now())
                .build();
    }
}