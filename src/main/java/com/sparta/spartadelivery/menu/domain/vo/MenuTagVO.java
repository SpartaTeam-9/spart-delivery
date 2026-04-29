package com.sparta.spartadelivery.menu.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@EqualsAndHashCode
public class MenuTagVO {
    // 메뉴 (N : 1)
    @Column(nullable = false)
    private UUID menuId;

    // 태그 (N : 1)
    @Column(nullable = false)
    private UUID tagId;

    public MenuTagVO(UUID menuId, UUID tagId) {
        validate(menuId, tagId);
        this.menuId = menuId;
        this.tagId = tagId;
    }

    // 추후 공통 검증 코드로 변경
    // EX) throw new InvalidMenuTagException("menuId는 null일 수 없습니다.");
    private static void validate(UUID menuId, UUID tagId) {
        if (menuId == null) {
            throw new IllegalArgumentException("menuId는 null일 수 없습니다.");
        }
        if (tagId == null) {
            throw new IllegalArgumentException("tagId는 null일 수 없습니다.");
        }
    }
}
