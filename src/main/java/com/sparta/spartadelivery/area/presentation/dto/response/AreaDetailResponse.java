package com.sparta.spartadelivery.area.presentation.dto.response;

import com.sparta.spartadelivery.area.domain.entity.Area;
import java.time.LocalDateTime;
import java.util.UUID;

public record AreaDetailResponse(
        UUID id,
        String name,
        String city,
        String district,
        boolean active,
        LocalDateTime createdAt
) {

    public static AreaDetailResponse from(Area area) {
        return new AreaDetailResponse(
                area.getId(),
                area.getName(),
                area.getCity(),
                area.getDistrict(),
                area.isActive(),
                area.getCreatedAt()
        );
    }
}
