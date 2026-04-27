package com.sparta.spartadelivery.menu.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class SelectionRangeVO {

    // DEFAULT 0
    @Column(nullable = false)
    private Integer minSelect;

    // DEFAULT 1
    @Column(nullable = false)
    private Integer maxSelect;

    public SelectionRangeVO(Integer minSelect, Integer maxSelect) {
        validate(minSelect, maxSelect);
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
    }

    private static void validate(Integer minSelect, Integer maxSelect) {
        if (minSelect == null || maxSelect == null) {
            throw new IllegalArgumentException("선택 범위는 null일 수 없습니다.");
        }
        if (minSelect < 0) {
            throw new IllegalArgumentException("minSelect는 0 이상이어야 합니다.");
        }
        // 생략 가능
        if (maxSelect < 1) {
            throw new IllegalArgumentException("maxSelect는 1 이상이어야 합니다.");
        }
        if (maxSelect < minSelect) {
            throw new IllegalArgumentException("maxSelect는 minSelect보다 작을 수 없습니다.");
        }
    }
}